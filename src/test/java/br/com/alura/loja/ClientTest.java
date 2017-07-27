package br.com.alura.loja;

import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import junit.framework.Assert;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ClientTest 
{
	
	private HttpServer server;
	private WebTarget target;
	private Client client;

	@Before
	public void before ()
	{	
		server = Servidor.inicializaServidor();
		
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.register(new LoggingFilter());
		
		client = ClientBuilder.newClient(clientConfig);
		String baseURL = "http://localhost:8080";
		
		target = client.target(baseURL);
	}
	
	@After
	public void after ()
	{
		server.stop();
	}
	
	@Test
	public void testeConexaoComServer()
	{
		Carrinho carrinho = target.path("/carrinhos/1").request().get(Carrinho.class);
		System.out.println("Aee carai...");
		System.out.println(carrinho.toXML());
		//Assert.assertTrue(rua.contains("Vergueiro"));
	}
	
	@Test
	public void testeQueSuportaNovosCarrinhos()
	{
		Carrinho carrinho = new Carrinho();
		carrinho.adiciona(new Produto(314, "Microfone", 37, 1));
		carrinho.setRua("Rua dos tabajós");
		carrinho.setCidade("Sum Paulo");
		String xml = carrinho.toXML();
		
		Entity<String> entity = Entity.entity(xml, MediaType.APPLICATION_XML);
        Response response = target.path("/carrinhos").request().post(entity);
        Assert.assertEquals(201, response.getStatus());
        
        String location = response.getHeaderString("Location");
        String conteudo = client.target(location).request().get(String.class);
        System.out.println(conteudo);
        Assert.assertTrue(conteudo.contains("Microfone"));
	}
}