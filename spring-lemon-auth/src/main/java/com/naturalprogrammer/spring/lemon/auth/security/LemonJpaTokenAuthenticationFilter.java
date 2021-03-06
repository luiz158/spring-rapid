package com.naturalprogrammer.spring.lemon.auth.security;

import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUser;
import com.naturalprogrammer.spring.lemon.auth.domain.dto.user.LemonUserDto;
import com.naturalprogrammer.spring.lemon.auth.security.service.BlueTokenService;
import com.naturalprogrammer.spring.lemon.auth.security.service.LemonUserDetailsService;
import com.naturalprogrammer.spring.lemon.auth.util.LemonUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.Serializable;

//Einfach impl von Filter der Brücke zu jpa macht
public class LemonJpaTokenAuthenticationFilter<U extends AbstractUser<ID>, ID extends Serializable>
	extends LemonCommonsWebTokenAuthenticationFilter {

    private static final Log log = LogFactory.getLog(LemonJpaTokenAuthenticationFilter.class);

    private LemonUserDetailsService<U, ID> userDetailsService;
	
	public LemonJpaTokenAuthenticationFilter(BlueTokenService blueTokenService,
											 LemonUserDetailsService<U, ID> userDetailsService) {
		
		super(blueTokenService);
		this.userDetailsService = userDetailsService;
		
		log.info("Created");		
	}

	@Override
	protected LemonUserDto fetchUserDto(JWTClaimsSet claims) {
		
        String username = claims.getSubject();
        U user = userDetailsService.findUserByUsername(username)
        		.orElseThrow(() -> new UsernameNotFoundException(username));

        log.debug("User found ...");

        LemonUtils.ensureCredentialsUpToDate(claims, user);
        LemonUserDto lemonUserDto = user.toUserDto();
        lemonUserDto.setPassword(null);
        
        return lemonUserDto;
	}
}
