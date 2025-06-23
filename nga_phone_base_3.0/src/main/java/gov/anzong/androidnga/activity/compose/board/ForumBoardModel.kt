package gov.anzong.androidnga.activity.compose.board

import com.alibaba.fastjson.JSON
import gov.anzong.androidnga.base.util.ContextUtils
import gov.anzong.androidnga.base.util.PreferenceUtils
import gov.anzong.androidnga.base.util.ThreadUtils
import gov.anzong.androidnga.common.PreferenceKey
import gov.anzong.androidnga.core.board.data.Board
import gov.anzong.androidnga.core.board.data.BoardEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Collections

class ForumBoardModel {

    private lateinit var boardList: MutableList<BoardEntity>

    lateinit var bookmark: BoardEntity

    private val boardMap: HashMap<String, BoardEntity> = HashMap()

    private val saveTask = kotlinx.coroutines.Runnable {
        CoroutineScope(Dispatchers.IO).launch {
            ForumBoardRepository.writeBoardList(
                ContextUtils.getContext(),
                boardList.toMutableList()
            )
        }
    }

    init {
        boardList = ForumBoardRepository.loadLocalBoardList(ContextUtils.getContext())
        boardList.forEach {
            initBoardData(it)
        }
        transferBookmarkBoards()
    }

    private fun initBoardData(boardEntity: BoardEntity) {
        if (boardEntity.type == BoardEntity.BoardType.BOOKMARK) {
            bookmark = boardEntity
        }
        boardMap[boardEntity.id] = boardEntity
        boardEntity.children?.let {
            it.forEach { data ->
                initBoardData(data)
            }
        }
    }

    fun loadBoardData(): MutableList<BoardEntity> {
        return boardList
    }

    fun addBookmarkBoard(fid: Int, stid: Int): Int {
        val id = computeBoardId(fid, stid)
        val boardEntity = boardMap[id]
        if (boardEntity != null && !bookmark.children!!.contains(boardEntity)) {
            bookmark.children!!.add(boardEntity)
            saveData()
        }
        return bookmark.children!!.size
    }

    fun removeBookmarkBoard(fid: Int, stid: Int): Int {
        val id = computeBoardId(fid, stid)
        val boardEntity = boardMap[id]
        if (boardEntity != null) {
            bookmark.children!!.remove(boardEntity)
            saveData()
        }
        return bookmark.children!!.size
    }

    private fun transferBookmarkBoards() {
        if (!bookmark.children.isNullOrEmpty()) {
            return
        }
        val bookmarkJson = PreferenceUtils.getData(PreferenceKey.BOOKMARK_BOARD, "")
        if (bookmarkJson.isNullOrEmpty()) {
            return
        }
        val bookmarks: List<Board> = JSON.parseArray(bookmarkJson, Board::class.java)

        bookmarks.forEach {
            val boardEntity = BoardEntity()
            boardEntity.fid = it.fid
            boardEntity.name = it.name.toString()
            if (it.stid != 0) {
                boardEntity.stid = it.stid
            }
            checkBoardData(boardEntity, bookmark)
            bookmark.children!!.add(boardEntity)
        }
        saveData(true)
        //PreferenceUtils.putData(PreferenceKey.BOOKMARK_BOARD, "")
    }

    private fun computeBoardId(fid: Int, stid: Int): String {
        var id = ""
        if (fid != 0) {
            id = fid.toString()
        }
        if (stid != 0) {
            id = id + "_" + stid
        }
        return id
    }

    private fun checkBoardData(board: BoardEntity, parent: BoardEntity?) {
        if (board.parentId.isNullOrEmpty()) {
            board.parentId = parent?.id
        }
        if (board.id.isEmpty()) {
            board.id = computeBoardId(board.fid, board.stid)
        }
    }

    fun swapBookmark(from: Int, to: Int) {
        val boards: List<BoardEntity> = bookmark.children!!
        if (from < to) {
            for (i in from until to) {
                Collections.swap(boards, i, i + 1)
            }
        } else {
            for (i in from downTo to + 1) {
                Collections.swap(boards, i, i - 1)
            }
        }
        saveData()
    }

    // 1s最多保存一次
    private fun saveData(rightNow: Boolean = false) {
        val delayTime = if (rightNow) 0 else 1000
        if (!ThreadUtils.hasRunnable(saveTask)) {
            ThreadUtils.postOnMainThreadDelay(saveTask, delayTime.toLong())
        }
    }

}