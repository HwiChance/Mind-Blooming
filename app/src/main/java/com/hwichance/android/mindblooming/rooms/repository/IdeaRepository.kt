package com.hwichance.android.mindblooming.rooms.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.hwichance.android.mindblooming.rooms.dao.IdeaDao
import com.hwichance.android.mindblooming.rooms.data.IdeaData
import com.hwichance.android.mindblooming.rooms.database.AppDatabase

class IdeaRepository(application: Application) {
    companion object {
        private var ideaRepoInstance: IdeaRepository? = null

        fun getInstance(app: Application): IdeaRepository {
            return ideaRepoInstance ?: synchronized(this) {
                ideaRepoInstance ?: IdeaRepository(app).also { ideaRepoInstance = it }
            }
        }
    }

    private val ideaDao: IdeaDao by lazy {
        val db = AppDatabase.getInstance(application.applicationContext)
        db.ideaDao()
    }
    private val ideaList: LiveData<List<IdeaData>> by lazy {
        ideaDao.getAll()
    }

    fun getAllIdeas(): LiveData<List<IdeaData>> {
        return ideaList
    }

    fun findIdeasByIds(ideaIds: List<Long>): LiveData<List<IdeaData>> {
        return ideaDao.getIdeasByIds(ideaIds)
    }

    fun findOneIdeaById(ideaId: Long): LiveData<IdeaData> {
        return ideaDao.getOneIdeaById(ideaId)
    }

    fun findStarredIdea(isStarred: Boolean): LiveData<List<IdeaData>> {
        return ideaDao.getStarredIdea(isStarred)
    }

    suspend fun insert(idea: IdeaData): Long {
        return ideaDao.insertIdea(idea)
    }

    suspend fun update(idea: IdeaData) {
        return ideaDao.updateIdea(idea)
    }

    suspend fun delete(idea: IdeaData) {
        ideaDao.deleteIdea(idea)
    }

    suspend fun deleteIdeas(ideaIds: List<Long>) {
        ideaDao.deleteIdeaInList(ideaIds)
    }
}