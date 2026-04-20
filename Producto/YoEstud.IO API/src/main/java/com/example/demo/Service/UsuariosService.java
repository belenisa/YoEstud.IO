package com.example.demo.Service;

import com.example.demo.Repositorio.UsuariosInterface;
import com.example.demo.Repositorio.UsuariosRepositorio;

@Transactional
@Service
//@SuppressWarnings("null")
public class UsuariosService {

    @Autowired //inyectar automáticamente una instancia del bean correspondiente en este campo.
    private UsuariosRepositorio usuariosInterface;

    public List<UsuariosModelo> findAll() {
        return usuariosInterface.findAll();
    }

    public UsuariosModelo findById(Long id) {
        UsuariosModelo artistas = artistasRepository.findById(id).orElse(null);
        return artistas;
    }
    
    public UsuariosModelo save(UsuariosModelo usuarios) {
        return usuariosInterface.save(usuarios);
    }

    public void deleteById(Long id) {
        usuariosInterface.deleteById(id);
    }

}
