package expose;
import business.HelpHomeService;
import config.Page;
import expose.dto.HelpHomeDto;
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

@Path("/help-home")
@Tag(name = "Ayuda de Casa", description = "Operaciones relacionadas con la ayuda de casa a misioneros.")
@RequiredArgsConstructor
public class HelpHomeResource {

    private final HelpHomeService helpHomeService;

    @GET
    @Path("/page/company/{companyId}/employee/{employeeId}")
    @Operation(summary = "Obtener todas las ayudas paginadas", description = "Obtiene una lista de ayudas paginadas.")
    @APIResponse(responseCode = "200", description = "Lista de ayudas obtenidas correctamente")
    @APIResponse(responseCode = "404", description = "Ayudas no encontradas")
    public Response findAllHelpHomePage(@PathParam("companyId") UUID companyId,
                                        @PathParam("employeeId") UUID employeeId,
                                        @QueryParam("search") @DefaultValue("") String search,
                                        @QueryParam("page") @Parameter(description = "Número de página", example = "0") @DefaultValue("0") Integer page,
                                        @QueryParam("size") @Parameter(description = "Tamaño de página", example = "10") @DefaultValue("10") Integer size) {
        Page<HelpHomeDto.Response> response = helpHomeService.findAllPage(companyId, employeeId, search, page, size);
        return Response.ok(response).build();
    }

    @GET
    @Path("/company/{companyId}/employee/{employeeId}/month/{month}/year/{year}")
    @Operation(summary = "Obtener todas las ayudas", description = "Obtiene una lista de ayudas.")
    @APIResponse(responseCode = "200", description = "Lista de ayudas obtenidas correctamente")
    @APIResponse(responseCode = "404", description = "Ayudas no encontradas")
    public Response findAllHelpHomeByEmployee(@PathParam("companyId") UUID companyId,
                                        @PathParam("employeeId") UUID employeeId,
                                        @PathParam("month") Integer month,
                                        @PathParam("year") Integer year) {
        HelpHomeDto.Response response = helpHomeService.findAllByEmployee(companyId, employeeId, month, year);
        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener ayuda por id", description = "Obtiene la ayuda por el id.")
    @APIResponse(responseCode = "200", description = "Ayuda obtenida correctamente")
    @APIResponse(responseCode = "404", description = "Ayuda no encontrada")
    public Response findByIdHelpHome(@PathParam("id") UUID id) {
        HelpHomeDto.Response object = helpHomeService.findById(id);
        return Response.ok(object).build();
    }

    @POST
    @Operation(summary = "Registrar nueva ayuda", description = "Registrar nueva ayuda.")
    @APIResponse(responseCode = "201", description = "Ayuda creada exitosamente")
    @APIResponse(responseCode = "400", description = "Datos de entrada inválidos")
    public Response saveHelpHome( @RequestBody HelpHomeDto.Request request) {
        HelpHomeDto.Response response = helpHomeService.save(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Modificar ayuda", description = "Modifica un ayuda existente.")
    @APIResponse(responseCode = "200", description = "Ayuda modificada correctamente")
    @APIResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @APIResponse(responseCode = "404", description = "Ayuda no encontrada")
    @APIResponse(responseCode = "409", description = "Conflicto (ejemplo: versión desactualizada)")
    public Response updateHelpHome(@PathParam("id") UUID id, @RequestBody HelpHomeDto.Request request) {
        HelpHomeDto.Response response = helpHomeService.update(id, request);
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar ayuda", description = "Elimina una ayuda existente por su ID")
    @APIResponse(responseCode = "204", description = "Ayuda eliminada correctamente")
    @APIResponse(responseCode = "404", description = "Ayuda no encontrada")
    public Response deleteHelpHome(@PathParam("id") UUID id) {
        helpHomeService.delete(id);
        return Response.noContent().build();
    }


}
