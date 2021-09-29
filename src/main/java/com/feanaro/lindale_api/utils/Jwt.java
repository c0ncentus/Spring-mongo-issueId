package com.feanaro.lindale_api.utils;
// package com.feanaro.lindale_api;

// import java.util.Map;
// import java.util.HashMap;
// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;
// import java.util.Date;
// import io.jsonwebtoken.SignatureAlgorithm;
// import java.util.function.Function;

// public class Jwt {
//     final private String SECRET_KEY = "JESUS_WILL_save_ALL_the_World_12Ever";

//     public String extractUsernam(String token) {
//         return extractClaim(token, Claims::getSubject);
//     }

//     public Date extractExpiration(String token){
//         return extractClaim(token, Claims::getExpiration);
//     }

//     public <T> T extractClaim(String token, Function<Claims,T> claimResolver){
//         final Claims claims = extractAllClaims(token);
//         return claimResolver.apply(claims);
//     }

//     private Claims extractAllClaims(String token) {
//         return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
//     }

//     public Boolean isTokenExpired(String token){
//         return extractExpiration(token).before(new Date());
//     }

//     public String  generateToken(String userName){
//         Map<String, Object> claims= new HashMap<String,Object>();
//         return createToken(claims, userName);
//     }

//     private String createToken(Map<String, Object> claims, String name) {
//         return Jwts.builder().setClaims(claims).setSubject(name).setIssuedAt(new Date(System.currentTimeMillis()))
//                 .setExpiration(new Date(System.currentTimeMillis() * 1000 * 60 * 60 * 24 * 30 * 12 * 3))
//                 .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
//     }
// }
