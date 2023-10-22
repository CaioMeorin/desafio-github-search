package br.com.igorbag.githubsearch.ui

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.domain.Repository
import br.com.igorbag.githubsearch.ui.adapter.RepositoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    lateinit var nomeUsuario: EditText
    lateinit var btnConfirmar: Button
    lateinit var listaRepositories: RecyclerView
    lateinit var githubApi: GitHubService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        showUserName()
        setupListeners()
        githubApi = setupRetrofit()
    }

    fun setupView() {
        nomeUsuario = findViewById(R.id.et_nome_usuario)
        btnConfirmar = findViewById(R.id.btn_confirmar)
        listaRepositories = findViewById(R.id.rv_lista_repositories)
    }

    private fun setupListeners() {
        btnConfirmar.setOnClickListener {
            saveUserLocal()
            getAllReposByUserName()
        }
    }

    private fun saveUserLocal() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        nomeUsuario.text.toString().let {
            editor.putString(getString(R.string.shared_pref_usuario), it)
            editor.apply()
        }
    }

    private fun showUserName() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        nomeUsuario.setText(sharedPref.getString(getString(R.string.shared_pref_usuario), ""))
    }

    fun setupRetrofit(): GitHubService {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.github.com/")
            .build()

        return retrofit.create(GitHubService::class.java)
    }

    private fun getAllReposByUserName() {
        githubApi.getAllRepositoriesByUser(nomeUsuario.text.toString()).enqueue(
            object : Callback<List<Repository>> {
                override fun onResponse(
                    call: Call<List<Repository>>,
                    response: Response<List<Repository>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            setupAdapter(it)
                        }
                    } else {
                        Toast.makeText(
                            baseContext,
                            "Something went wrong, try again later.",
                            Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                    Toast.makeText(
                        baseContext,
                        "Something went wrong, try again later.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    fun setupAdapter(list: List<Repository>) {
        val repositoryAdapter = RepositoryAdapter(baseContext, repositories = list)
        listaRepositories.apply {
            adapter = repositoryAdapter
        }
    }
}