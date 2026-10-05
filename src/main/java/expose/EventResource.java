package expose;

import business.EventService;
import config.Page;
import expose.dto.EventDto;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Path("/event")
@Tag(name = "Event", description = "Operations related to events and their recurrence.")
@RequiredArgsConstructor
public class EventResource {

    private final EventService eventService;

    @GET
    @Path("/page/company/{companyId}")
    @Operation(summary = "Get paginated events", description = "Gets a paginated list of events.")
    @APIResponse(responseCode = "200", description = "Event list retrieved successfully")
    public Response findAllPage(@PathParam("companyId") UUID companyId,
                                 @QueryParam("search") @DefaultValue("") String search,
                                 @QueryParam("page") @Parameter(description = "Page number", example = "0") @DefaultValue("0") Integer page,
                                 @QueryParam("size") @Parameter(description = "Page size", example = "10") @DefaultValue("10") Integer size) {
        Page<EventDto.Response> response = eventService.findAllPage(companyId, search, page, size);
        return Response.ok(response).build();
    }

    @GET
    @Path("/company/{companyId}")
    @Operation(summary = "Get all events", description = "Gets the full list of events for the company.")
    @APIResponse(responseCode = "200", description = "Event list retrieved successfully")
    public Response findAllByCompanyId(@PathParam("companyId") UUID companyId) {
        return Response.ok(eventService.findAllByCompanyId(companyId)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get event by id", description = "Gets the event by its id.")
    @APIResponse(responseCode = "200", description = "Event retrieved successfully")
    @APIResponse(responseCode = "404", description = "Event not found")
    public Response findById(@PathParam("id") UUID id) {
        return Response.ok(eventService.findById(id)).build();
    }

    @POST
    @Operation(summary = "Create event", description = "Creates a new event with its recurrence type.")
    @APIResponse(responseCode = "201", description = "Event created successfully")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    public Response save(@RequestBody EventDto.Request request) {
        EventDto.Response response = eventService.save(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Update event", description = "Updates an existing event.")
    @APIResponse(responseCode = "200", description = "Event updated successfully")
    @APIResponse(responseCode = "404", description = "Event not found")
    public Response update(@PathParam("id") UUID id, @RequestBody EventDto.Request request) {
        return Response.ok(eventService.update(id, request)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete event", description = "Deletes an existing event by its ID")
    @APIResponse(responseCode = "204", description = "Event deleted successfully")
    @APIResponse(responseCode = "404", description = "Event not found")
    public Response delete(@PathParam("id") UUID id) {
        eventService.delete(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/people")
    @Operation(summary = "Get event people", description = "Gets the specific/guest people ids associated to this event (meaning depends on personSelectionMode).")
    @APIResponse(responseCode = "200", description = "Person id list retrieved successfully")
    public Response findEventPeople(@PathParam("id") UUID id) {
        return Response.ok(eventService.findEventPersonIds(id)).build();
    }

    @PUT
    @Path("/{id}/people")
    @Transactional
    @Operation(summary = "Set event people", description = "Replaces the specific/guest people list of this event.")
    @APIResponse(responseCode = "204", description = "Event people updated successfully")
    public Response setEventPeople(@PathParam("id") UUID id, @RequestBody List<UUID> personIds) {
        eventService.setEventPersonIds(id, personIds);
        return Response.noContent().build();
    }
}
