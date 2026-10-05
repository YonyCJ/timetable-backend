package expose;

import business.AssignmentService;
import expose.dto.AssignmentDto;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Path("/assignment")
@Tag(name = "Assignment", description = "Assignment of people to the parts of an event occurrence, manual or automatic.")
@RequiredArgsConstructor
public class AssignmentResource {

    private final AssignmentService assignmentService;

    @GET
    @Path("/occurrence/{eventOccurrenceId}")
    @Operation(summary = "Get assignments for an occurrence", description = "Gets the assignments (person per part) of an event occurrence.")
    @APIResponse(responseCode = "200", description = "Assignment list retrieved successfully")
    public Response findAllByEventOccurrenceId(@PathParam("eventOccurrenceId") UUID eventOccurrenceId) {
        return Response.ok(assignmentService.findAllByEventOccurrenceId(eventOccurrenceId)).build();
    }

    @POST
    @Path("/manual")
    @Transactional
    @Operation(summary = "Assign person manually", description = "Manually assigns (or replaces) the person in charge of a part in an occurrence.")
    @APIResponse(responseCode = "200", description = "Assignment saved successfully")
    public Response assignManually(@RequestBody AssignmentDto.Request request) {
        return Response.ok(assignmentService.assignManually(request)).build();
    }

    @POST
    @Path("/draw")
    @Transactional
    @Operation(summary = "Draw assignments automatically", description = "Randomly draws available people to cover each part of an occurrence, avoiding repeating a person on the same day.")
    @APIResponse(responseCode = "200", description = "Draw completed successfully")
    @APIResponse(responseCode = "400", description = "Not enough parts or people to draw from")
    public Response draw(@RequestBody AssignmentDto.DrawRequest request) {
        return Response.ok(assignmentService.draw(request)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete assignment", description = "Deletes an existing assignment by its ID")
    @APIResponse(responseCode = "204", description = "Assignment deleted successfully")
    @APIResponse(responseCode = "404", description = "Assignment not found")
    public Response delete(@PathParam("id") UUID id) {
        assignmentService.delete(id);
        return Response.noContent().build();
    }
}
