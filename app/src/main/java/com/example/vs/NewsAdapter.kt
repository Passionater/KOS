import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.vs.databinding.ItemNewsBinding // 본인의 패키지 이름 확인
import com.example.vs.R

class NewsAdapter(private val newsList: List<NewsItem>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    // 1. 각 아이템의 뷰를 보관하는 클래스
    inner class NewsViewHolder(private val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(newsItem: NewsItem) {
            // 뉴스 제목 설정
            binding.newsTitleTextView.text = newsItem.title
            // 이미지 URL을 통해 이미지 로드 (Coil 라이브러리 사용)
            binding.newsImageView.load(newsItem.imageUrl) {
                crossfade(true) // 부드럽게 이미지가 나타나는 효과
                placeholder(R.drawable.ic_launcher_background) // 로딩 중에 보여줄 이미지
                error(R.drawable.ic_launcher_foreground) // 에러 시 보여줄 이미지
            }
        }
    }

    // 2. 뷰 홀더를 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    // 3. 뷰 홀더에 데이터를 바인딩(연결)하는 함수
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(newsList[position])
    }

    // 4. 전체 아이템 개수를 반환하는 함수
    override fun getItemCount(): Int {
        return newsList.size
    }
}