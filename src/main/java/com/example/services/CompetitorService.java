package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Competitor;
import com.example.models.CompetitorDTO;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response.Status;

@Path("/competitors")
@Produces(MediaType.APPLICATION_JSON)
public class CompetitorService {

    @PersistenceContext(unitName = "CompetitorsPU")
    EntityManager entityManager;

    @PostConstruct
    public void init() {
        try {
            entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(CompetitorDTO loginData) {
        try {
            // Obtener el email y la contraseña del objeto CompetitorDTO
            String email = loginData.getEmail();
            String password = loginData.getPassword();

            // Buscar al competidor en la base de datos usando el email
            Query query = entityManager.createQuery("SELECT c FROM Competitor c WHERE c.email = :email");
            query.setParameter("email", email);
            Competitor competitor = (Competitor) query.getSingleResult();

            // Verificar si la contraseña coincide
            if (competitor != null && competitor.getPassword().equals(password)) {
                return Response.status(Status.OK)
                        .header("Access-Control-Allow-Origin", "*")
                        .entity(competitor)
                        .build();
            } else {
                // Responder con un error de autorización si las credenciales no coinciden
                return Response.status(Status.UNAUTHORIZED)
                        .entity("Invalid email or password")
                        .build();
            }
        } catch (Exception e) {
            // Manejo de excepciones en caso de que ocurra un error en la consulta o el proceso
            return Response.status(Status.UNAUTHORIZED)
                    .entity("Invalid email or password")
                    .build();
        }
    }
}
