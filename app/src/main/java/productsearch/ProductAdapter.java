package productsearch;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import com.example.nokiaphonerecognizer.R;
import productsearch.ProductAdapter.ProductViewHolder;
import java.util.List;

/** Presents the list of product items from cloud product search. */
public class ProductAdapter extends Adapter<ProductViewHolder> {

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        static ProductViewHolder create(ViewGroup parent) {
            View view =
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
            return new ProductViewHolder(view);
        }

        private final ImageView imageView;
        private final TextView titleView;
        private final TextView subtitleView;
        private final int imageSize;

        private ProductViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.product_image);
            titleView = view.findViewById(R.id.product_title);
            subtitleView = view.findViewById(R.id.product_subtitle);
            imageSize = view.getResources().getDimensionPixelOffset(R.dimen.product_item_image_size);
        }

        void bindProduct(Product product) {
            imageView.setImageDrawable(null);
            if (!TextUtils.isEmpty(product.imageUrl)) {
                new ImageDownloadTask(imageView, imageSize).execute(product.imageUrl);
            } else {
                imageView.setImageResource(R.drawable.logo_cloud);
            }
            titleView.setText(product.title);
            subtitleView.setText(product.subtitle);
        }
    }

    private final List<Product> productList;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @Override
    @NonNull
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ProductViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bindProduct(productList.get(position));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
