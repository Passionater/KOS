// FoodLabelAdapter.kt
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vs.databinding.ItemFoodLabelBinding
import android.content.Intent // 👈 추가
import com.example.vs.DetailActivity
import com.example.vs.FoodInfo

class FoodLabelAdapter(private val infos: List<FoodInfo?>) : RecyclerView.Adapter<FoodLabelAdapter.LabelViewHolder>() {

    inner class LabelViewHolder(private val binding: ItemFoodLabelBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FoodInfo?) {
            if (item != null) {
                binding.foodLabelTextView.text = item.name?:"" // 리스트에 이름 넣는 부분

                // 👇 아이템 클릭 리스너 추가
                // 클릭시 디테일 페이지에 해당 정보값 전달하는 부분
                itemView.setOnClickListener {
                    val context = it.context
                    val intent = Intent(context, DetailActivity::class.java)
                    // 클릭한 음식 정보 전달
                    intent.putExtra("foodName", item.name)
                    intent.putExtra("foodCarbohydrate", item.carbohydrate)
                    intent.putExtra("foodProtein", item.protein)
                    intent.putExtra("foodFat", item.fat)
                    intent.putExtra("foodGiRate", item.giRate)
                    intent.putExtra("foodTotalDietaryFiber", item.totalDietaryFiber)
                    intent.putExtra("foodImageUrl", item.foodImageUrl)
                    context.startActivity(intent)
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        val binding = ItemFoodLabelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LabelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        holder.bind(infos[position])
    }

    override fun getItemCount() = infos.size
}