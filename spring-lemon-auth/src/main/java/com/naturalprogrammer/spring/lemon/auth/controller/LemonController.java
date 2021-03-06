package com.naturalprogrammer.spring.lemon.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUser;
import com.naturalprogrammer.spring.lemon.auth.domain.dto.ChangePasswordForm;
import com.naturalprogrammer.spring.lemon.auth.domain.dto.LemonSignupForm;
import com.naturalprogrammer.spring.lemon.auth.domain.dto.RequestEmailChangeForm;
import com.naturalprogrammer.spring.lemon.auth.domain.dto.ResetPasswordForm;
import com.naturalprogrammer.spring.lemon.auth.domain.dto.user.LemonAdminUpdateUserDto;
import com.naturalprogrammer.spring.lemon.auth.domain.dto.user.LemonFetchForeignByEmailDto;
import com.naturalprogrammer.spring.lemon.auth.domain.dto.user.LemonReadUserDto;
import com.naturalprogrammer.spring.lemon.auth.domain.dto.user.LemonUserDto;
import com.naturalprogrammer.spring.lemon.auth.properties.LemonProperties;
import com.naturalprogrammer.spring.lemon.auth.service.LemonService;
import com.naturalprogrammer.spring.lemon.auth.util.LecUtils;
import com.naturalprogrammer.spring.lemon.auth.util.LecwUtils;
import com.naturalprogrammer.spring.lemon.auth.util.LemonUtils;
import com.naturalprogrammer.spring.lemon.exceptions.util.LexUtils;
import io.github.vincemann.springrapid.acl.Role;
import io.github.vincemann.springrapid.acl.service.Secured;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.Direction;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingInfo;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.RapidDtoEndpoint;
import io.github.vincemann.springrapid.core.controller.rapid.RapidController;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import io.github.vincemann.springrapid.core.slicing.components.WebComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

/**
 * The Lemon API. See the
 * <a href="https://github.com/naturalprogrammer/spring-lemon#documentation-and-resources">
 * API documentation</a> for details.
 *
 * @author Sanjay Patel
 *
 */
@WebComponent
public abstract class LemonController
	<U extends AbstractUser<ID>, ID extends Serializable>
			extends RapidController<U,ID,LemonService<U, ID,?>> {

	private static final Log log = LogFactory.getLog(LemonController.class);

    private long jwtExpirationMillis;


	@Autowired
	public void injectProperties(LemonProperties properties){
		this.jwtExpirationMillis = properties.getJwt().getExpirationMillis();
	}

	@Autowired
	@Secured
	@Override
	public void injectCrudService(LemonService<U, ID, ?> crudService) {
		super.injectCrudService(crudService);
	}

	/**
	 * A simple function for pinging this server.
	 */
	@GetMapping("/ping")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void ping() {

		log.debug("Received a ping");
	}


	/**
	 * Returns context properties needed at the client side,
	 * current-user data and an Authorization token as a response header.
	 */
	@GetMapping("/context")
	@ResponseBody
	public Map<String, Object> getContext(
			@RequestParam Optional<Long> expirationMillis,
			HttpServletResponse response) {

		log.debug("Getting context ");
		Map<String, Object> context = getService().getContext(expirationMillis, response);
		log.debug("Returning context: " + context);

		return context;
	}

	@Override
	public DtoMappingContext provideDtoMappingContext() {
		return LemonDtoMappingContextBuilder.builder()
				.forAll(LemonUserDto.class)
				.forResponse(LemonReadUserDto.class)
				.forEndpoint(LemonDtoEndpoint.SIGN_UP, Direction.REQUEST, LemonSignupForm.class)
				.withPrincipal(DtoMappingInfo.Principal.FOREIGN)
				.forEndpoint(LemonDtoEndpoint.FETCH_BY_EMAIL,Direction.RESPONSE, LemonFetchForeignByEmailDto.class)
				.withAllPrincipals()
				.withRoles(Role.ADMIN)
				.forEndpoint(RapidDtoEndpoint.UPDATE, LemonAdminUpdateUserDto.class)
				.build();
	}

	/**
	 * Signs up a user, and
	 * returns current-user data and an Authorization token as a response header.
	 */
	@PostMapping("/users")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public ResponseEntity<String> signup(/*@RequestBody @JsonView(UserUtils.SignupInput.class) S signupForm,*/
							   HttpServletRequest request,
							   HttpServletResponse response) throws BadEntityException, IOException, EntityNotFoundException {

		String signupForm = readBody(request);
		Object signupDto = getJsonMapper().readValue(signupForm, findDtoClass(LemonDtoEndpoint.SIGN_UP, Direction.REQUEST, null));
		getValidationStrategy().validateDto(signupDto);
		log.debug("Signing up: " + signupDto);
		U user = (U) getDtoMapper().mapToEntity(signupDto, getEntityClass());
		U saved = getService().signup(user);
		log.debug("Signed up: " + signupForm);

		addAuthHeader(response,saved);
		Object dto = getDtoMapper().mapToDto(saved,
				findDtoClass(LemonDtoEndpoint.SIGN_UP, Direction.RESPONSE, saved.getId()));
		return ok(getJsonMapper().writeValueAsString(dto));
	}


	/**
	 * Resends verification mail
	 */
	@PostMapping("/users/{id}/resend-verification-mail")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void resendVerificationMail(@PathVariable("id") U user) {

		log.debug("Resending verification mail for: " + user);
		getService().resendVerificationMail(user);
		log.debug("Resent verification mail for: " + user);
	}


	/**
	 * Verifies current-user -> send code per email
	 */
	@PostMapping("/users/{id}/verification")
	@ResponseBody
	public ResponseEntity<String> verifyUser(
			@PathVariable ID id,
			@RequestParam String code,
			HttpServletResponse response) throws JsonProcessingException, BadEntityException {
		getValidationStrategy().validateId(id);
		log.debug("Verifying user ...");
		U saved = getService().verifyUser(id, code);

		addAuthHeader(response,saved);
		Object dto = getDtoMapper().mapToDto(saved,
				findDtoClass(LemonDtoEndpoint.VERIFY_USER, Direction.RESPONSE, saved.getId()));
		return ok(getJsonMapper().writeValueAsString(dto));
	}


	/**
	 * The forgot Password feature -> mail new password to email
	 */
	@PostMapping("/forgot-password")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void forgotPassword(@RequestParam String email) {

		log.debug("Received forgot password request for: " + email);
		getService().forgotPassword(email);
	}


	/**
	 * Resets password after it's forgotten
	 */
	@PostMapping("/reset-password")
	@ResponseBody
	public ResponseEntity<String> resetPassword(
			@RequestBody ResetPasswordForm form,
			HttpServletResponse response) throws JsonProcessingException, BadEntityException {

		log.debug("Resetting password ... ");
		U saved = getService().resetPassword(form);
		addAuthHeader(response,saved);
		Object dto = getDtoMapper().mapToDto(saved,
				findDtoClass(LemonDtoEndpoint.RESET_PASSWORD, Direction.RESPONSE, saved.getId()));
		return ok(getJsonMapper().writeValueAsString(dto));
	}


	/**
	 * Fetches a user by email
	 */
	@PostMapping("/users/fetch-by-email")
	@ResponseBody
	public ResponseEntity<String> fetchUserByEmail(@RequestParam String email) throws JsonProcessingException, BadEntityException {

		log.debug("Fetching user by email: " + email);
		U byEmail = getService().findByEmail(email);
		LexUtils.ensureFound(byEmail);
		Object responseDto = getDtoMapper().mapToDto(byEmail,
				findDtoClass(RapidDtoEndpoint.FIND, Direction.RESPONSE, byEmail.getId()));
		return ok(getJsonMapper().writeValueAsString(responseDto));
	}




//	//todo remove and replace with rapidController update endpoint entirely...
//	/**
//	 * Updates a user
//	 */
//	@PatchMapping("/users/{id}")
//	@ResponseBody
//	public LemonUserDto authUpdate(
//			@PathVariable("id") ID userId,
//			@RequestBody String patch,
//			HttpServletResponse response)
//			throws IOException, JsonPatchException, BadEntityException, EntityNotFoundException, DtoMappingException {
//
//		log.debug("Updating user ... ");
//
//		// ensure that the user exists
//		LexUtils.ensureFound(userId);
//		Optional<U> byId = getCrudService().findById(userId);
//		U updateUser = LmapUtils.applyPatch(byId.get(), patch); // create a patched form
//		//default security Rule checks for write permission
//		U updated = getCrudService().updateUser(byId.get(), updateUser);
//		LemonUserDto dto = updated.toUserDto();
//		dto.setPassword(null);
//		// Send a new token for logged in user in the response
//
//		return dto;
//	}


	@Override
	public void afterUpdate(Object dto, U updated, HttpServletRequest httpServletRequest, HttpServletResponse response) {
		super.afterUpdate(dto, updated, httpServletRequest, response);
		addAuthHeader(response,updated);
	}

//	@Override
//	protected U serviceUpdate(U update, boolean full) throws BadEntityException, EntityNotFoundException {
//		U updated = super.serviceUpdate(update, full);
//		//set password should not trigger immediate update
//		updated.setPassword(null);
//		return updated;
//	}

	/**
	 * Changes password
	 */
	@PostMapping("/users/{id}/password")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void changePassword(@PathVariable("id") U user,
			@RequestBody ChangePasswordForm changePasswordForm,
			HttpServletResponse response) {

		log.debug("Changing password ... ");
		String username = getService().changePassword(user, changePasswordForm);
		//todo warum gibts das im service und im controller?
		getService().addAuthHeader(response, username, jwtExpirationMillis);
	}


	/**
	 * Requests for changing email
	 */
	@PostMapping("/users/{id}/email-change-request")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void requestEmailChange(@PathVariable("id") ID userId,
								   @RequestBody RequestEmailChangeForm emailChangeForm) {

		log.debug("Requesting email change ... ");
		getService().requestEmailChange(userId, emailChangeForm);
	}


	/**
	 * Changes the email
	 */
	@PostMapping("/users/{userId}/email")
	@ResponseBody
	public ResponseEntity<String> changeEmail(
			@PathVariable ID userId,
			@RequestParam String code,
			HttpServletResponse response) throws JsonProcessingException, BadEntityException {

		log.debug("Changing email of user ...");
		U saved = getService().changeEmail(userId, code);
		addAuthHeader(response,saved);
		Object responseDto = getDtoMapper().mapToDto(saved,
				findDtoClass(LemonDtoEndpoint.CHANGE_EMAIL, Direction.RESPONSE, userId));
		return ok(getJsonMapper().writeValueAsString(responseDto));
	}


	/**
	 * Fetch a new token - for session sliding, switch user etc.
	 *
	 */
	@PostMapping("/fetch-new-auth-token")
	@ResponseBody
	public Map<String, String> fetchNewToken(
			@RequestParam Optional<Long> expirationMillis,
			@RequestParam Optional<String> username,
			HttpServletResponse response) {

		log.debug("Fetching a new token ... ");
		return LecUtils.mapOf("token", getService().fetchNewToken(expirationMillis, username));
	}


	/**
	 * Fetch a self-sufficient token with embedded UserDto - for interservice communications
	 */
	@GetMapping("/fetch-full-token")
	@ResponseBody
	public Map<String, String> fetchFullToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {

		log.debug("Fetching a micro token");
		return getService().fetchFullToken(authHeader);
	}


	/**
	 * returns the current user and puts a new authorization token in the response
	 */
	protected void addAuthHeader(HttpServletResponse response, U saved) {
		LemonUtils.login(saved);
		LemonUserDto currentUser = LecwUtils.currentUser();
		getService().addAuthHeader(response, currentUser.getEmail(), jwtExpirationMillis);
	}
}
