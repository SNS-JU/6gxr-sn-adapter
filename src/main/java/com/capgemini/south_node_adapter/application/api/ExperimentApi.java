/**
 * NOTE: This class is auto generated by the swagger code generator program (3.0.63).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package com.capgemini.south_node_adapter.application.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.capgemini.south_node_adapter.domain.model.ExperimentError;
import com.capgemini.south_node_adapter.domain.model.NetworkServiceTemplate;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2024-10-17T14:53:34.212638557Z[GMT]")
@Validated
public interface ExperimentApi {

	@Operation(summary = "", description = "Delete experiment from the database.", security = {
			@SecurityRequirement(name = "snAdapterOAuth", scopes = {}) }, tags = { "NST Management" })
	@ApiResponses(value = { @ApiResponse(responseCode = "202", description = "Experiment data deleted.") })
	@RequestMapping(value = "/experiment/{experimentName}", method = RequestMethod.DELETE)
	ResponseEntity<Void> experimentExperimentNameDelete(
			@NotNull @Parameter(in = ParameterIn.QUERY, description = "", required = true, schema = @Schema()) @Valid @RequestParam(value = "sessionId", required = true) String sessionId,
			@Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema()) @PathVariable("experimentName") String experimentName);

	@Operation(summary = "", description = "Query Network Service Template virtualization process.", security = {
			@SecurityRequirement(name = "snAdapterOAuth", scopes = {}) }, tags = { "NST Management" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Check status of current experiment.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NetworkServiceTemplate.class))) })
	@RequestMapping(value = "/experiment/{experimentName}", produces = {
			"application/json" }, method = RequestMethod.GET)
	ResponseEntity<NetworkServiceTemplate> experimentExperimentNameGet(
			@NotNull @Parameter(in = ParameterIn.QUERY, description = "", required = true, schema = @Schema()) @Valid @RequestParam(value = "sessionId", required = true) String sessionId,
			@Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema()) @PathVariable("experimentName") String experimentName);

	@Operation(summary = "", description = "Query all experiments created by the user.", security = {
			@SecurityRequirement(name = "snAdapterOAuth", scopes = {}) }, tags = { "NST Management" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Check status of current experiment.", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = NetworkServiceTemplate.class)))) })
	@RequestMapping(value = "/experiment", produces = { "application/json" }, method = RequestMethod.GET)
	ResponseEntity<List<NetworkServiceTemplate>> experimentGet(
			@NotNull @Parameter(in = ParameterIn.QUERY, description = "", required = true, schema = @Schema()) @Valid @RequestParam(value = "sessionId", required = true) String sessionId);

	@Operation(summary = "", description = "Save the experiment with the NST.", security = {
			@SecurityRequirement(name = "snAdapterOAuth", scopes = {}) }, tags = { "NST Management" })
	@ApiResponses(value = { @ApiResponse(responseCode = "202", description = "Experiment saved.") })
	@RequestMapping(value = "/experiment", consumes = { "application/json" }, method = RequestMethod.POST)
	ResponseEntity<Void> experimentPost(
			@NotNull @Parameter(in = ParameterIn.QUERY, description = "", required = true, schema = @Schema()) @Valid @RequestParam(value = "sessionId", required = true) String sessionId,
			@Parameter(in = ParameterIn.DEFAULT, description = "", schema = @Schema()) @Valid @RequestBody NetworkServiceTemplate body);

	@Operation(summary = "", description = "Start experiment virtualization process.", security = {
			@SecurityRequirement(name = "snAdapterOAuth", scopes = {}) }, tags = { "NST Management" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Instances created by launched experiment."),

			@ApiResponse(responseCode = "4XX", description = "At least one of the instances failed.", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ExperimentError.class)))),

			@ApiResponse(responseCode = "5XX", description = "At least one of the instances failed.", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ExperimentError.class)))) })
	@RequestMapping(value = "/experiment/run/{experimentName}", produces = {
			"application/json" }, method = RequestMethod.POST)
	ResponseEntity<List<ExperimentError>> experimentRunExperimentNamePost(
			@NotNull @Parameter(in = ParameterIn.QUERY, description = "", required = true, schema = @Schema()) @Valid @RequestParam(value = "sessionId", required = true) String sessionId,
			@Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema()) @PathVariable("experimentName") String experimentName);

	@Operation(summary = "", description = "Finish experiment virtualization process.", security = {
			@SecurityRequirement(name = "snAdapterOAuth", scopes = {}) }, tags = { "NST Management" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Instances created by launched experiment."),

			@ApiResponse(responseCode = "4XX", description = "At least one of the instances failed.", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ExperimentError.class)))),

			@ApiResponse(responseCode = "5XX", description = "At least one of the instances failed.", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ExperimentError.class)))) })
	@RequestMapping(value = "/experiment/terminate/{experimentName}", produces = {
			"application/json" }, method = RequestMethod.DELETE)
	ResponseEntity<List<ExperimentError>> experimentTerminateExperimentNameDelete(
			@NotNull @Parameter(in = ParameterIn.QUERY, description = "", required = true, schema = @Schema()) @Valid @RequestParam(value = "sessionId", required = true) String sessionId,
			@Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema()) @PathVariable("experimentName") String experimentName);

}
