package med.voll.api.controller;

import jakarta.validation.Valid;
import med.voll.api.medico.AtualizaMedicoDTO;
import med.voll.api.medico.DadosDetalhamentoMedico;
import med.voll.api.medico.ListagemMedicoDTO;
import med.voll.api.medico.Medico;
import med.voll.api.medico.MedicoDTO;
import med.voll.api.medico.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("medicos")
public class MedicoController {

    @Autowired
    private MedicoRepository medicoRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoMedico> cadastrar(@RequestBody @Valid MedicoDTO dados, UriComponentsBuilder uriComponentsBuilder) {
        var medico = medicoRepository.save(new Medico(dados));
        var uri = uriComponentsBuilder.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoMedico(medico));
    }

    @GetMapping
    public ResponseEntity<Page<ListagemMedicoDTO>> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        return ResponseEntity.ok(medicoRepository.findAllByAtivoTrue(paginacao).map(ListagemMedicoDTO::new));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoMedico> detalhar(@PathVariable Long id) {
        var medico = medicoRepository.getReferenceById(id);

        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
    }

    @PutMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoMedico> alterar(@RequestBody @Valid AtualizaMedicoDTO dados) {
        Medico medico = medicoRepository.getReferenceById(dados.id());
        medico.atualizarInformacoes(dados);

        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity excluir(@PathVariable Long id) {
        medicoRepository.getReferenceById(id).excluir();

        return ResponseEntity.noContent().build();
    }
}
