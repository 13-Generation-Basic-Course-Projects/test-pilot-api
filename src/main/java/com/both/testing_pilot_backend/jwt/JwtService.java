package com.both.testing_pilot_backend.jwt;

import com.both.testing_pilot_backend.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

/**
 * JwtService handles the creation, parsing, and validation of JWT tokens
 * using a symmetric secret key. It is responsible for extracting claims
 * from the token and ensuring the token is valid and not expired.
 */
@Component
public class JwtService {

	public static final long JWT_TOKEN_VALIDITY =  7 * 24 * 60 * 60; // 5 hours in seconds
	public static final String SECRET = "FVPr6Q/fVlHGZkElZubC0Zaxv657dPUfDQ4o9DADjSin7+uST1d2A5klMWrMK8fmSl3doyf2wn5zj56VC+qqCg==";

	public String createToken(Map<String, Object> claim, String subject) {
		return Jwts.builder()
				.claims(claim)
				.subject(subject)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000)) // Expiration in milliseconds
				.signWith(getSignKey()).compact();
	}

	private SecretKey getSignKey() {
		byte[] keyBytes = Base64.getDecoder().decode(SECRET);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	//2. generate token for user
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		User user = (User) userDetails; // Assuming UserDetails can be cast to your User model
		claims.put("user_id", user.getUserId().toString()); // Ensure user.getId() is used and converted to String
		return createToken(claims, user.getEmail());
	}

	//3. retrieving any information from token we will need the secret key
	public Claims extractAllClaim(String token) {
		return Jwts.parser()
				.verifyWith(getSignKey())
				.build()
				.parseSignedClaims(token)
				.getPayload(); // getPayload() returns Claims, which is a Map<String, Object>
	}

	//4. extract a specific claim from the JWT token’s claims.
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaim(token);
		return claimsResolver.apply(claims);
	}

	//5. retrieve username from jwt token (subject)
	public String extractEmail(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	//6. retrieve expiration date from jwt token
	public Date extractExpirationDate(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	//7. check expired token
	private Boolean isTokenExpired(String token) {
		return extractExpirationDate(token).before(new Date());
	}

	//8. validate token
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String email = extractEmail(token);
		return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	public String generatePublicShareToken(UUID userSharedId, LocalDateTime expireAt){
		Map<String, Object> claims = new HashMap<>();
		claims.put("usId", userSharedId.toString());
		claims.put("ea", expireAt.toString()); // Serialize LocalDateTime to ISO string
		return createToken(claims, userSharedId.toString());
	}

	// NEW: generateInvitationToken - now correctly sets subject and claims
	public String generateInvitationToken(UUID projectCollaboratorId, UUID invitedUserId, UUID projectId) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("pcId", projectCollaboratorId.toString()); // ProjectCollaborator link ID
		claims.put("pId", projectId.toString()); // Project ID

		// The 'uId' claim is no longer strictly necessary as 'invitedUserId' is now the 'subject'
		// However, keeping it doesn't hurt and provides redundancy/clarity.
		claims.put("uId", invitedUserId.toString()); // Invited User ID

		// The subject of the invitation token should be the invited user's ID.
		// This is crucial for the verify endpoint to check if the logged-in user is the intended recipient.
		return createToken(claims, invitedUserId.toString()); // Subject is the invited user's ID
	}
}
