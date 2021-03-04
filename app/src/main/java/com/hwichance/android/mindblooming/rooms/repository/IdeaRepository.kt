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

    fun findIdeaInSeries(seriesId: Long): LiveData<List<IdeaData>> {
        return ideaDao.getIdeaInSeries(seriesId)
    }

    suspend fun insert(idea: IdeaData): Long {
        return ideaDao.insertIdea(idea)
    }

    suspend fun update(idea: IdeaData) {
        ideaDao.updateIdea(idea)
    }

    suspend fun updateStar(isStarred: Boolean, ideaIds: List<Long>) {
        ideaDao.updateNotStarred(isStarred, ideaIds)
    }

    suspend fun updateSeries(ideaIds: List<Long>) {
        ideaDao.updateNoSeries(ideaIds)
    }

    suspend fun updateSeriesDelete(seriesIds: List<Long>) {
        ideaDao.updateSeriesDeleted(seriesIds)
    }

    suspend fun delete(idea: IdeaData) {
        ideaDao.deleteIdea(idea)
    }

    suspend fun deleteIdeas(ideaIds: List<Long>) {
        ideaDao.deleteIdeaInList(ideaIds)
    }
}