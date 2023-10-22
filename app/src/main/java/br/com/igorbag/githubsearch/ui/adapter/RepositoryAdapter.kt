package br.com.igorbag.githubsearch.ui.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.domain.Repository

class RepositoryAdapter(private val context: Context, private val repositories: List<Repository>) :
    RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

    var repoItemListener: (Repository) -> Unit = {}
    var btnShareListener: (Repository) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.repository_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.name.text = repositories[position].name

        holder.card.setOnClickListener {
            repoItemListener(repositories[position])
            openBrowser(repositories[position].htmlUrl)
        }

        holder.share.setOnClickListener() {
            btnShareListener(repositories[position])
            shareRepositoryLink(repositories[position].htmlUrl)
        }
    }

    override fun getItemCount(): Int = repositories.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView
        val share: ImageView
        val card: CardView

        init {
            view.apply {
                name = findViewById(R.id.tv_preco)
                share = findViewById(R.id.iv_favorite)
                card = findViewById(R.id.cv_car)
            }

        }
    }

    fun openBrowser(urlRepository: String) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    fun shareRepositoryLink(urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }
        val shareIntent =
            Intent.createChooser(sendIntent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)

    }
}

