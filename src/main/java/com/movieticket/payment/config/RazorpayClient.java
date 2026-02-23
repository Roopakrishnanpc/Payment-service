package com.movieticket.payment.config;

public class RazorpayClient {

}
//package com.movieticket.payment.config;
//
//import com.razorpay.RazorpayClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class RazorpayConfig {
//
//  @Value("${razorpay.key-id}")
//  private String keyId;
//
//  @Value("${razorpay.key-secret}")
//  private String keySecret;
//
//  @Bean
//  public RazorpayClient razorpayClient() throws Exception {
//      return new RazorpayClient(keyId, keySecret);
//  }
//}
//import org.json.JSONObject;
//import com.razorpay.Order;
//
//JSONObject orderRequest = new JSONObject();
//orderRequest.put("amount", request.getAmount().multiply(BigDecimal.valueOf(100)).intValue()); 
//orderRequest.put("currency", "INR");
//orderRequest.put("receipt", "booking_" + request.getBookingId());
//
//Order order = razorpayClient.orders.create(orderRequest);
//
//String razorpayOrderId = order.get("id");
//
//PaymentTransaction transaction = PaymentTransaction.builder()
//      .bookingId(request.getBookingId())
//      .userId(request.getUserId())
//      .amount(request.getAmount())
//      .status(PaymentStatus.PENDING)
//      .transactionReference(razorpayOrderId)
//      .createdAt(LocalDateTime.now())
//      .build();
//
//paymentRepository.save(transaction);
//@PostMapping("/webhook")
//public ResponseEntity<Void> handleWebhook(@RequestBody String payload,
//                                         @RequestHeader("X-Razorpay-Signature") String signature) {
//  // Verify signature
//  // Update payment status to CHARGED
//  return ResponseEntity.ok().build();
//}