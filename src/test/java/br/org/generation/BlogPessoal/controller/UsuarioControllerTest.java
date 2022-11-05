package br.org.generation.BlogPessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.org.generation.BlogPessoal.model.Usuario;
import br.org.generation.BlogPessoal.repository.UsuarioRepository;
import br.org.generation.BlogPessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsuarioControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@BeforeAll
	void start() {
		
		usuarioRepository.deleteAll();
		
	}
	
	@Test
	@Order(1)
	@DisplayName("cadastrar um usuario")
	public void deveCriarUmUsuario() {
		
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(new Usuario(0L,
				"paulo antunes", "algfoto", "usuario@paulo.com", "12345678" ));
		
		ResponseEntity<Usuario> resposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST, requisicao, Usuario.class);
		
	assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
	assertEquals(requisicao.getBody().getNome(), resposta.getBody().getNome());
	assertEquals(requisicao.getBody().getFoto(), resposta.getBody().getFoto());
	assertEquals(requisicao.getBody().getUsuario(), resposta.getBody().getUsuario());
				
	}
	
	@Test
	@Order(2)
	@DisplayName("nao deve permitir duplicacao de usuario")
	public void naoDeveDuplicarUsuario() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"maria da silva", "url foto", "maria@gmail.com", "123456"));
	
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(new Usuario(0L,
				"maria da silva", "url foto", "maria@gmail.com", "123456"));
		ResponseEntity<Usuario> resposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST, requisicao, Usuario.class);
		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
		
		
	}
	
	@Test
	@Order(3)
	@DisplayName("alterar um usuario")
	public void deveAtualizarUsuario() {
		Optional<Usuario> usuarioCreate = usuarioService.cadastrarUsuario(new Usuario(0L,
				"juliana rews", "url foto", "juliana@Gmail.com", "123456"));
		Usuario usuarioUpdate = new Usuario(usuarioCreate.get().getId(),
				"juliana andramos", "url foto atualizada", "juliasjnsadj@gmail.com", "123456789");
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuarioUpdate);
		
		ResponseEntity<Usuario> resposta = testRestTemplate
				.withBasicAuth("root", "root")
				.exchange("/usuarios/atualizar", HttpMethod.PUT, requisicao, Usuario.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertEquals(usuarioUpdate.getNome(), resposta.getBody().getNome());
		assertEquals(usuarioUpdate.getFoto(), resposta.getBody().getFoto());
		assertEquals(usuarioUpdate.getUsuario(), resposta.getBody().getUsuario());
		
	}
	
	@Test
	@Order(4)
	@DisplayName("listar todos usuarios")
	public void listarTodosUsuuarios() {
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"sabrina sanches", "url foto", "sabrina@gmail.com", "123456"));
	
	usuarioService.cadastrarUsuario(new Usuario(0L,
			"ricardo marques", "url foto", "ricardo@gmail1.com", "123456"));
	
	ResponseEntity<String> resposta = testRestTemplate
			.withBasicAuth("root", "root")
			.exchange("/usuarios/all", HttpMethod.GET, null, String.class);
	assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
	
	
}
