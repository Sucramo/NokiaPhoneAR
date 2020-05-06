package productsearch;

public class Product {
    final String imageUrl;
    final String title;
    final String subtitle;

    Product(String imageUrl, String title, String subtitle) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.subtitle = subtitle;
    }
}
