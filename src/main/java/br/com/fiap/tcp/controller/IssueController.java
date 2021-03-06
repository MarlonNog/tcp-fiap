package br.com.fiap.tcp.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.tcp.model.Issue;
import br.com.fiap.tcp.model.Message;
import br.com.fiap.tcp.repository.IssueRepository;
import br.com.fiap.tcp.service.IssueProducer;

@RestController
public class IssueController {

	private final IssueProducer producer;

	@Autowired
	private IssueRepository issueRepository;

	@Autowired
	public IssueController(IssueProducer producer) {
		this.producer = producer;
	}

	@PostMapping(value = "/issues")
	public ResponseEntity<Message> createIssueWithKafka(@RequestBody Issue issue) {
		Message mensagem = null;
		try {
			UUID uuid = UUID.randomUUID();
			issue.setProtocol(uuid.toString());
			this.producer.sendMessageIssue(issue);
			mensagem = new Message("Seu chamado foi criado com sucesso. Anote o seu protocolo: " + issue.getProtocol());
			return new ResponseEntity<Message>(mensagem, HttpStatus.CREATED);

		} catch (Exception e) {
			mensagem = new Message("Erro ao processar a sua solicitação!");
			return new ResponseEntity<Message>(mensagem, HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}
	
	@GetMapping(value= "/issues")
	public Issue findByIssueByProtocol(@RequestParam(value="protocol") String protocol) {
		Issue issue = issueRepository.findByProtocol(protocol);
		return issue;
	}


}