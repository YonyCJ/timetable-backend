package expose;

import business.EventOccurrenceService;
import expose.dto.EventOccurrenceDto;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.LocalDate;
import java.util.UUID;

@Path("/event-occurrence")
@Tag(name = "Event Occurrence", description = "Concrete instances of an event on specific dates, generated from its recurrence.")
@RequiredArgsConstructor
public class EventOccurrenceResource {

    private final EventOccurrenceService eventOccurrenceService;

    @GET
    @Path("/event/{eventId}")
    @Operation(summary = "Get an event's occurrences", description = "Gets all generated occurrences of an event.")
    @APIResponse(responseCode = "200", description = "Occurrence list retrieved successfully")
    public Response findAllByEventId(@PathParam("eventId") UUID eventId,
                                      @QueryParam("from") String from,
                                      @QueryParam("to") String to) {
        if (from != null && to != null) {
            return Response.ok(eventOccurrenceService.findAllByEventIdAndRange(
                    eventId, LocalDate.parse(from), LocalDate.parse(to))).build();
        }
        return Response.ok(eventOccurrenceService.findAllByEventId(eventId)).build();
    }

    @POST
    @Path("/generate")
    @Transactional
    @Operation(summary = "Generate occurrences", description = "Generates an event's occurrences in a date range, according to its recurrence type.")
    @APIResponse(responseCode = "200", description = "Occurrences generated successfully")
    public Response generate(@RequestBody EventOccurrenceDto.GenerateRequest request) {
        return Response.ok(eventOccurrenceService.generate(
                request.getEventId(), request.getFrom(), request.getTo())).build();
    }

    @PUT
    @Path("/{id}/cancel")
    @Transactional
    @Operation(summary = "Cancel occurrence", description = "Marks a single date as not held (e.g. social activity) without affecting the recurrence rule.")
    @APIResponse(responseCode = "200", description = "Occurrence cancelled successfully")
    @APIResponse(responseCode = "404", description = "Occurrence not found")
    public Response cancel(@PathParam("id") UUID id, @RequestBody EventOccurrenceDto.CancelRequest request) {
        return Response.ok(eventOccurrenceService.cancel(id, request)).build();
    }

    @PUT
    @Path("/{id}/reactivate")
    @Transactional
    @Operation(summary = "Reactivate occurrence", description = "Reverts the cancellation of an occurrence.")
    @APIResponse(responseCode = "200", description = "Occurrence reactivated successfully")
    @APIResponse(responseCode = "404", description = "Occurrence not found")
    public Response reactivate(@PathParam("id") UUID id, @QueryParam("lastModifiedBy") String lastModifiedBy) {
        return Response.ok(eventOccurrenceService.reactivate(id, lastModifiedBy)).build();
    }
}
