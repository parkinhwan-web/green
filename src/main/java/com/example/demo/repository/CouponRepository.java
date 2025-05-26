public interface CouponRepository extends JpaRepository<Coupon, Long> {
    List<Coupon> findByAvailableTrue(); // 사용 가능한 것만
}
