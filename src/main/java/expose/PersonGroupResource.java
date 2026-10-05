package expose;

import business.PersonGroupService;
import expose.dto.PersonGroupDto;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Path("/person-group")
@Tag(name = "Person Group", description = "Groups/areas used to organize people and restrict who is eligible for an event's draw.")
@RequiredArgsConstructor
public class PersonGroupResource {

    private final PersonGroupService personGroupService;

    @GET
    @Path("/company/{companyId}")
    @Operation(summary = "Get all groups", description = "Gets the full list of person groups for the company.")
    @APIResponse(responseCode = "200", description = "Group list retrieved successfully")
    public Response findAllByCompanyId(@PathParam("companyId") UUID companyId) {
        return Response.ok(personGroupService.findAllByCompanyId(companyId)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get group by id", description = "Gets the person group by its id.")
    @APIResponse(responseCode = "200", description = "Group retrieved successfully")
    @APIResponse(responseCode = "404", description = "Group not found")
    public Response findById(@PathParam("id") UUID id) {
        return Response.ok(personGroupService.findById(id)).build();
    }

    @POST
    @Operation(summary = "Create group", description = "Creates a new person group.")
    @APIResponse(responseCode = "201", description = "Group created successfully")
    public Response save(@RequestBody PersonGroupDto.Request request) {
        PersonGroupDto.Response response = personGroupService.save(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Update group", description = "Updates an existing person group.")
    @APIResponse(responseCode = "200", description = "Group updated successfully")
    @APIResponse(responseCode = "404", description = "Group not found")
    public Response update(@PathParam("id") UUID id, @RequestBody PersonGroupDto.Request request) {
        return Response.ok(personGroupService.update(id, request)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete group", description = "Deletes an existing person group by its ID")
    @APIResponse(responseCode = "204", description = "Group deleted successfully")
    @APIResponse(responseCode = "404", description = "Group not found")
    public Response delete(@PathParam("id") UUID id) {
        personGroupService.delete(id);
        return Response.noContent().build();
    }
}
