package expose;

import business.EventPartService;
import expose.dto.EventPartDto;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Path("/event-part")
@Tag(name = "Event Part", description = "Dynamic configurable parts within an event (e.g. prayer, sermon, activity).")
@RequiredArgsConstructor
public class EventPartResource {

    private final EventPartService eventPartService;

    @GET
    @Path("/event/{eventId}")
    @Operation(summary = "Get parts of an event", description = "Gets the list of parts configured for an event, ordered.")
    @APIResponse(responseCode = "200", description = "Part list retrieved successfully")
    public Response findAllByEventId(@PathParam("eventId") UUID eventId) {
        return Response.ok(eventPartService.findAllByEventId(eventId)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get part by id", description = "Gets the event part by its id.")
    @APIResponse(responseCode = "200", description = "Part retrieved successfully")
    @APIResponse(responseCode = "404", description = "Part not found")
    public Response findById(@PathParam("id") UUID id) {
        return Response.ok(eventPartService.findById(id)).build();
    }

    @POST
    @Operation(summary = "Create part", description = "Adds a dynamic part to an event.")
    @APIResponse(responseCode = "201", description = "Part created successfully")
    public Response save(@RequestBody EventPartDto.Request request) {
        EventPartDto.Response response = eventPartService.save(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Update part", description = "Updates an existing event part.")
    @APIResponse(responseCode = "200", description = "Part updated successfully")
    @APIResponse(responseCode = "404", description = "Part not found")
    public Response update(@PathParam("id") UUID id, @RequestBody EventPartDto.Request request) {
        return Response.ok(eventPartService.update(id, request)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete part", description = "Deletes an existing event part by its ID")
    @APIResponse(responseCode = "204", description = "Part deleted successfully")
    @APIResponse(responseCode = "404", description = "Part not found")
    public Response delete(@PathParam("id") UUID id) {
        eventPartService.delete(id);
        return Response.noContent().build();
    }
}
