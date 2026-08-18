package com.example.data.repository

import com.example.data.local.CategoryDao
import com.example.data.model.TaskCategory
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {
    val allCategories: Flow<List<TaskCategory>> = categoryDao.getAllCategories()

    suspend fun getCategoryById(id: Long): TaskCategory? = categoryDao.getCategoryById(id)

    suspend fun insertCategory(category: TaskCategory): Long = categoryDao.insertCategory(category)

    suspend fun insertCategories(categories: List<TaskCategory>) = categoryDao.insertCategories(categories)

    suspend fun updateCategory(category: TaskCategory) = categoryDao.updateCategory(category)

    suspend fun deleteCategory(category: TaskCategory) = categoryDao.deleteCategory(category)

    suspend fun deleteCategoryById(id: Long) = categoryDao.deleteCategoryById(id)

    suspend fun getAllCategoriesDirect(): List<TaskCategory> = categoryDao.getAllCategoriesDirect()

    suspend fun clearAll() = categoryDao.clearAll()
}
