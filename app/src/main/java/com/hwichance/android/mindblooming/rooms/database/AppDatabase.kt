package com.hwichance.android.mindblooming.rooms.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.converter.IdeaTypeConverter
import com.hwichance.android.mindblooming.rooms.dao.IdeaDao
import com.hwichance.android.mindblooming.rooms.dao.MindMapItemDao
import com.hwichance.android.mindblooming.rooms.dao.SeriesDao
import com.hwichance.android.mindblooming.rooms.data.IdeaData
import com.hwichance.android.mindblooming.rooms.data.MindMapItemData
import com.hwichance.android.mindblooming.rooms.data.SeriesData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [MindMapItemData::class, IdeaData::class, SeriesData::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(IdeaTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mindMapItemDao(): MindMapItemDao
    abstract fun ideaDao(): IdeaDao
    abstract fun seriesDao(): SeriesDao

    companion object {
        private const val DB_NAME = "mind_blooming_db"
        private var dbInstance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return dbInstance ?: synchronized(AppDatabase::class) {
                dbInstance ?: buildDatabase(context)
            }
        }

        // TODO: decide logic in onCreate
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val date = System.currentTimeMillis()
                            val data = SeriesData(
                                null,
                                context.getString(R.string.drawer_series_one),
                                context.getString(R.string.drawer_series_one),
                                date,
                                date,
                                true
                            )
                            getInstance(context).seriesDao().insertSeries(data)

                            data.seriesTitle = context.getString(R.string.drawer_series_two)
                            data.seriesDescription = context.getString(R.string.drawer_series_two)
                            getInstance(context).seriesDao().insertSeries(data)

                            data.seriesTitle = context.getString(R.string.drawer_series_three)
                            data.seriesDescription = context.getString(R.string.drawer_series_three)
                            getInstance(context).seriesDao().insertSeries(data)
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                .also { dbInstance = it }
        }

        fun destroyInstance() {
            dbInstance = null
        }
    }
}