@Entity
@Table(name = "coupons")
public class Coupon {
    @Id @GeneratedValue private Long id;
    private String brandName;
    private String productName;
    private int points;
    private String category;
    private String imageUrl;
    private int expireDays;
    private boolean available;
    private String description;
}
