package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAll(object : PostRepository.PostsCallback<List<Post>> {

            override fun onSuccess(data: List<Post>) {
                _data.postValue(FeedModel(posts = data, empty = data.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
        repository.save(edited.value, object : PostRepository.PostsCallback<Post> {
            override fun onSuccess(data: Post) {
                _postCreated.postValue(Unit)
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(id: Long, likedByMe: Boolean) {
        repository.likeById(id, likedByMe, object : PostRepository.PostsCallback<Post> {
            override fun onSuccess(data: Post) {
                _data.postValue(
                    FeedModel(
                        posts = _data.value?.posts.orEmpty()
                            .map {
                                if (it.id == id) data else it
                            })
                )
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun removeById(id: Long) {
        repository.removeById(id, object : PostRepository.PostsCallback<Post> {
            override fun onSuccess(data: Post) {
                _data.postValue(
                    FeedModel(
                        posts = _data.value?.posts.orEmpty()
                            .filter { it.id != id }
                    )
                )
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }
}




