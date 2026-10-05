package expose;

import business.PersonService;
import config.Page;
import expose.dto.PersonDto;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Path("/person")
@Tag(name = "Person", description = "People that can be assigned to event parts.")
@RequiredArgsConstructor
public class PersonResource {

    private final PersonService personService;

    @GET
    @Path("/page/company/{companyId}")
    @Operation(summary = "Get paginated people", description = "Gets a paginated list of people.")
    @APIResponse(responseCode = "200", description = "Person list retrieved successfully")
    public Response findAllPage(@PathParam("companyId") UUID companyId,
                                 @QueryParam("search") @DefaultValue("") String search,
                                 @QueryParam("page") @Parameter(description = "Page number", example = "0") @DefaultValue("0") Integer page,
                                 @QueryParam("size") @Parameter(description = "Page size", example = "10") @DefaultValue("10") Integer size) {
        Page<PersonDto.Response> response = personService.findAllPage(companyId, search, page, size);
        return Response.ok(response).build();
    }

    @GET
    @Path("/company/{companyId}")
    @Operation(summary = "Get all people", description = "Gets the full list of people for the company.")
    @APIResponse(responseCode = "200", description = "Person list retrieved successfully")
    public Response findAllByCompanyId(@PathParam("companyId") UUID companyId) {
        return Response.ok(personService.findAllByCompanyId(companyId)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get person by id", description = "Gets the person by their id.")
    @APIResponse(responseCode = "200", description = "Person retrieved successfully")
    @APIResponse(responseCode = "404", description = "Person not found")
    public Response findById(@PathParam("id") UUID id) {
        return Response.ok(personService.findById(id)).build();
    }

    @POST
    @Operation(summary = "Create person", description = "Registers a new person.")
    @APIResponse(responseCode = "201", description = "Person created successfully")
    public Response save(@RequestBody PersonDto.Request request) {
        PersonDto.Response response = personService.save(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Update person", description = "Updates an existing person.")
    @APIResponse(responseCode = "200", description = "Person updated successfully")
    @APIResponse(responseCode = "404", description = "Person not found")
    public Response update(@PathParam("id") UUID id, @RequestBody PersonDto.Request request) {
        return Response.ok(personService.update(id, request)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete person", description = "Deletes an existing person by their ID")
    @APIResponse(responseCode = "204", description = "Person deleted successfully")
    @APIResponse(responseCode = "404", description = "Person not found")
    public Response delete(@PathParam("id") UUID id) {
        personService.delete(id);
        return Response.noContent().build();
    }
}
