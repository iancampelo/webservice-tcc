package br.com.ucb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import br.com.ucb.factory.ConnectionFactory;
import br.com.ucb.modelo.Usuario;

/**
 * Classe responsavel por realizar as operacoes na base de dados pertinentes a usuario
 * @author Jean
 *
 */
public class UsuarioDao extends ConnectionFactory{

	private static UsuarioDao instancia = null;

	/**
	 * Metodo responsavel por criar uma instancia da classe seguindo o padrao singleton
	 * 
	 * @return AtividadeDao
	 */
	public static UsuarioDao getInstancia() {
		if (instancia == null) {
			instancia = new UsuarioDao();
		}
		return instancia;
	}
	
	/**
	 * Metodo responsavel por inserir um usuario no banco de dados
	 * 
	 * @param Usuario
	 * @return boolean
	 */
	public boolean inserir(Usuario user) {

		Connection conn = null;
		PreparedStatement ps = null;
		
		String sql = "insert into usuario(usuario, senha, nome, nascimento,funcao) "
				+ "values (?,?,?,?,?)";
		try {
			conn = criarConexao();
			ps = conn.prepareStatement(sql);
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

			ps.setString(1, user.getUsuario());
			ps.setString(2, user.getSenha());
			ps.setString(3, user.getNome());
			ps.setDate  (4,  new java.sql.Date(format.parse(user.getNascimento()).getTime()));
			ps.setString(5, user.getFuncao());

			return ps.executeUpdate() > 0;

		} catch (Exception e) {
			System.out.println("Erro ao inserir usuario: " + e);
			e.printStackTrace();
			return false;
		} finally {
			fecharConexao(conn, ps, null);
		}
	}

	/**
	 * Metodo responsavel por excluir uma usuario do banco de dados
	 * 
	 * @param Usuario
	 * @return boolean
	 */
	public boolean excluir(Usuario user) {
		Connection conn = null;
		PreparedStatement ps = null;

		String sql = "delete from usuario where usuario = ?";

		try {
			conn = criarConexao();
			ps = conn.prepareStatement(sql);

			ps.setString(1, user.getUsuario());

			return ps.executeUpdate() > 0;
			
		} catch (Exception e) {
			System.out.println("Erro ao excluir usuario: " + e);
			e.printStackTrace();
			return false;
		} finally {
			fecharConexao(conn, ps, null);
		}
	}

	/**
	 * Metodo responsavel por consultar um usuario no banco de dados
	 * 
	 * @param Usuario
	 * @return Usuario
	 */
	public Usuario consultar(String usuario) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Usuario user = null;

		String sql = "select * from usuario where usuario = ?";

		try {
			conn = criarConexao();
			ps = conn.prepareStatement(sql);
			ps.setString(1, usuario);

			rs = ps.executeQuery();

			if (rs.next()) {
				user = new Usuario();
				
				user.setId        (rs.getInt   ("id"));
				user.setUsuario   (rs.getString("usuario"));
				user.setSenha     (rs.getString("senha"));
				user.setNome      (rs.getString("nome"));
				user.setNascimento(rs.getDate  ("nascimento").toString());
				user.setFuncao    (rs.getString("funcao"));
				
				AtividadeDao ativDao = AtividadeDao.getInstancia();
				user.setAtividades(ativDao.listar(user));
	
				return user;
			} else {
				return user;
			}
		} catch (Exception e) {
			System.out.println("Erro ao consultar Usuario: " + e);
			e.printStackTrace();
			return null;
		} finally {
			fecharConexao(conn, ps, rs);
		}
	}

	/**
	 * Metodo responsavel por atualizar um usuario
	 * 
	 * @param Usuario
	 * @return boolean
	 */
	public boolean atualizar(Usuario user) {

		Connection conn = null;
		PreparedStatement ps = null;

		String sql = "update usuario set "+
				     "senha = ?, nome = ?, nascimento = ?, funcao = ? "+
				     "where usuario = ?";
		try {
			conn = criarConexao();
			ps = conn.prepareStatement(sql);
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			
			ps.setString(1, user.getSenha());
			ps.setString(2, user.getNome());
			ps.setDate  (3,  new java.sql.Date(format.parse(user.getNascimento()).getTime()));
			ps.setString(4, user.getFuncao()); 
			ps.setString(5, user.getUsuario());
			
			return ps.executeUpdate() > 0;

		} catch (Exception e) {
			System.out.println("Erro ao atualizar usuario: " + e);
			e.printStackTrace();
			return false;
		} finally {
			fecharConexao(conn, ps, null);
		}
	}
	
}
