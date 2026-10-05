package expose;

import business.PersonUnavailabilityService;
import expose.dto.PersonUnavailabilityDto;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Path("/person-unavailability")
@Tag(name = "Person Unavailability", description = "Availability restrictions (recurring weekday, single date, or date range) of a person.")
@RequiredArgsConstructor
public class PersonUnavailabilityResource {

    private final PersonUnavailabilityService service;

    @GET
    @Path("/person/{personId}")
    @Operation(summary = "Get restrictions for a person", description = "Gets the availability restrictions of a person.")
    @APIResponse(responseCode = "200", description = "List retrieved successfully")
    public Response findAllByPersonId(@PathParam("personId") UUID personId) {
        return Response.ok(service.findAllByPersonId(personId)).build();
    }

    @POST
    @Operation(summary = "Create restriction", description = "Registers an availability restriction for a person.")
    @APIResponse(responseCode = "201", description = "Restriction created successfully")
    public Response save(@RequestBody PersonUnavailabilityDto.Request request) {
        PersonUnavailabilityDto.Response response = service.save(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete restriction", description = "Deletes an existing availability restriction by its ID")
    @APIResponse(responseCode = "204", description = "Restriction deleted successfully")
    @APIResponse(responseCode = "404", description = "Restriction not found")
    public Response delete(@PathParam("id") UUID id) {
        service.delete(id);
        return Response.noContent().build();
    }
}
