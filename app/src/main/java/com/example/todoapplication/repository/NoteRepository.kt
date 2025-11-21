package com.example.todoapplication.repository

import com.example.todoapplication.data.Note
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NoteRepository(
    private val db: FirebaseFirestore = Firebase.firestore
) {
    private fun col(uid: String) =
        db.collection("users").document(uid).collection("notes")
    /** 실시간 목록 (최신순) */
    fun listenNotes(uid: String, pageSize: Long = 50) = callbackFlow<List<Note>> {
        val reg = col(uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(pageSize)
            .addSnapshotListener { snap, err ->
                if (err != null) {
// 에러 시 빈 리스트로 방어하거나 close(err)
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snap?.documents?.mapNotNull { d ->
                    d.toObject<Note>()?.copy(id = d.id)
                }.orEmpty()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }
    /** 초기 페이지 로드(스냅샷 + 다음 페이지 토대용) */
    suspend fun loadFirstPage(uid: String, pageSize: Long = 20)
            : Pair<List<Note>, DocumentSnapshot?> {
        val q = col(uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(pageSize)
        val snap = q.get().await()
        val notes = snap.documents.mapNotNull { d -> d.toObject<Note>()?.copy(id = d.id) }
        val last = snap.documents.lastOrNull()
        return notes to last
    }
    /** 다음 페이지 */
    suspend fun loadNextPage(uid: String, last: DocumentSnapshot, pageSize: Long = 20)
            : Pair<List<Note>, DocumentSnapshot?> {
        val q = col(uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .startAfter(last)
            .limit(pageSize)
        val snap = q.get().await()
        val notes = snap.documents.mapNotNull { d -> d.toObject<Note>()?.copy(id = d.id) }
        val next = snap.documents.lastOrNull()
        return notes to next
    }
    /** 생성 */
    suspend fun add(uid: String, title: String, content: String): String {
        val data = hashMapOf(
            "title" to title,
            "content" to content,
            "createdAt" to Timestamp.now(),
            "updatedAt" to Timestamp.now()
        )
        val ref = col(uid).add(data).await()
        return ref.id
    }
    /** 업데이트 (부분 업데이트) */
    suspend fun update(uid: String, id: String, title: String?, content: String?) {
        val m = mutableMapOf<String, Any>(
            "updatedAt" to Timestamp.now()
        )
        title?.let { m["title"] = it }
        content?.let { m["content"] = it }
        col(uid).document(id).update(m).await()
    }
    /** 삭제 */
    suspend fun delete(uid: String, id: String) {
        col(uid).document(id).delete().await()
    }
    /** 트랜잭션 예: 카운터 증가(예시) */
    suspend fun incrementCounter(uid: String, id: String) {
        db.runTransaction { tx ->
            val ref = col(uid).document(id)
            val snap = tx.get(ref)
            val current = snap.getLong("count") ?: 0L
            tx.update(ref, mapOf("count" to current + 1, "updatedAt" to Timestamp.now()))
        }.await()
    }
}