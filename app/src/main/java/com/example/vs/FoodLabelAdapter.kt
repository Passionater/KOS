// FoodLabelAdapter.kt
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vs.databinding.ItemFoodLabelBinding
import android.content.Intent // 👈 추가
import com.example.vs.DetailActivity

class FoodLabelAdapter(private val labels: List<String?>) : RecyclerView.Adapter<FoodLabelAdapter.LabelViewHolder>() {

    inner class LabelViewHolder(private val binding: ItemFoodLabelBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(label: String?) {
            binding.foodLabelTextView.text = label

            // 👇 아이템 클릭 리스너 추가

            itemView.setOnClickListener {
                val context = it.context
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("foodName", label) // 클릭한 음식 이름을 전달
                context.startActivity(intent)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        val binding = ItemFoodLabelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LabelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        holder.bind(labels[position])
    }

    override fun getItemCount() = labels.size
}