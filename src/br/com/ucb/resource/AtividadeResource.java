package br.com.ucb.resource;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.TimeTypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;

import br.com.ucb.dao.AtividadeDao;
import br.com.ucb.dao.UsuarioDao;
import br.com.ucb.modelo.Atividade;
import br.com.ucb.modelo.Usuario;

/**
 * Classe do Web Service que recebera as informcoes pertinentes a atividades e realizara as operacoes
 * desejadas na base de dados
 * @author Jean Silvestre
 *
 */
@Path("atividade")
public class AtividadeResource{
	@GET
	@Path("/hello")
	@Produces(MediaType.APPLICATION_JSON)
	public String hello(){		
		System.out.println(System.currentTimeMillis());
		return "hello";
	}

	/**
	 * Metodo responsavel por traduzir uma String Json em uma atividade e inserir esta atividade atraves do DAO
	 * @param Json String (Atividade)
	 */
	@POST
	@Path("cadastrarAtividade")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String cadastrarAtividade(String ativStr) throws Exception{		
		AtividadeDao adao = null;

		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		adao = adao.getInstancia();

		Atividade ativ = gson.fromJson(ativStr, Atividade.class);

		String newAtiv = ativStr.replace('\n', ' ').trim();
		ativ = getTimes(ativ,newAtiv);

		return adao.inserir(ativ).toString();
	}

	public Atividade getTimes(Atividade ativ, String ativStr) throws JSONException{
		JSONObject jsonObj = new JSONObject(ativStr);
		String tempoEst = jsonObj.getString("tempoEstimado");
		if(tempoEst != null){
			if(!tempoEst.contains("null")){
				if(!tempoEst.isEmpty()){
					ativ.setTempoEstimado(Time.valueOf(tempoEst));
				}
			}
		}

		if(!jsonObj.isNull("tempoGasto")){
			String tmpG = jsonObj.getString("tempoGasto");
			if(tmpG != null){
				if(!tmpG.contains("null")){
					if(!tmpG.isEmpty()){
						ativ.setTempoGasto(Time.valueOf(tmpG));
					}
				}
			}
		}
		else
			ativ.setTempoGasto(Time.valueOf("00:00:00"));
		return ativ;
	}

	/**
	 * Metodo responsavel por traduzir uma String Json em uma atividade e alterar esta atividade atraves do DAO
	 * @param Json String (Atividade)
	 * @throws Exception 
	 */
	@POST
	@Path("alterarAtividade")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String alterarAtividade(String ativStr) throws Exception{		
		AtividadeDao adao = null;
		Atividade atividade = new Atividade();
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().serializeNulls().create();
		adao = adao.getInstancia();

		atividade = gson.fromJson(ativStr, Atividade.class);

		atividade = getTimes(atividade, ativStr);

		return adao.atualizar(atividade)? "S" : "N";
	}

	/**
	 * Metodo responsavel por traduzir uma String Json em uma atividade e excluir esta atividade atraves do DAO
	 * @param Json String (Atividade)
	 * @throws Exception 
	 */
	@POST
	@Path("excluirAtividade")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String excluirAtividade(String ativStr) throws Exception{		
		AtividadeDao adao = null;
		Atividade atividade = new Atividade();
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().serializeNulls().create();

		adao = adao.getInstancia();

		atividade = gson.fromJson(ativStr, Atividade.class);

		atividade = getTimes(atividade, ativStr);

		return adao.excluir(atividade)?"S":"N";
	}

	/**
	 * Metodo responsavel por traduzir uma String Json em uma atividade e consultar esta atividade atraves do DAO
	 * @param Json String (Atividade)
	 * @return atividade
	 * @throws Exception 
	 */
	@POST
	@Path("consultarAtividade")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Atividade consultarAtividade(String ativStr) throws Exception{		
		AtividadeDao adao = null;
		Atividade atividade = new Atividade();
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().serializeNulls().create();

		adao = adao.getInstancia();

		atividade = gson.fromJson(ativStr, Atividade.class);

		atividade = getTimes(atividade, ativStr);

		atividade = adao.consultar(atividade);

		return atividade;
	}

	/**
	 * Metodo responsavel por traduzir uma String Json em todas as atividades do usuario
	 * @param Json String (Usuario)
	 * @return atividades
	 * @throws Exception 
	 */
	@POST
	@Path("listarAtividades")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Atividade> listarAtividades(String usuario) throws Exception{		
		AtividadeDao adao = null;
		ArrayList<Atividade> atividades = new ArrayList<Atividade>();
		UsuarioDao udao = null;
		Usuario user = new Usuario();
		Gson gsonU = new Gson();
		udao = udao.getInstancia();
		user = gsonU.fromJson(usuario, Usuario.class);
		user = udao.consultar(user.getUsuario());

		adao = adao.getInstancia();
		atividades = adao.listar(user);
		return atividades;
	}


	/**
	 * Metodo responsavel por definir o indice KMA para uma atividade
	 * @param Json String (Usuario)
	 * @return String valor kma
	 * @throws Exception 
	 */
	@POST
	@Path("getKmaMedio")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getKmaMedio(String userStr) throws Exception{
		AtividadeDao adao = null;
		Atividade atividade = null;
		Usuario usuario = new Usuario();
		ArrayList<Atividade> atividades = new ArrayList<Atividade>();
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().serializeNulls().create();

		float paramA = 0; // numero de vezes que disse que teria sucesso e teve
		float paramB = 0; // numero de vezes que disse que nao teria sucesso e teve
		float paramC = 0; // numero de vezes que disse que teria sucesso e não teve
		float paramD = 0; // numero de vezes que disse que nao teria sucesso e nao teve

		adao = adao.getInstancia();

		usuario = gson.fromJson(userStr, Usuario.class);
		atividades = adao.listar(usuario);

		for (Atividade ativ : atividades) {
			if((ativ.getPredicao() == 1d && ativ.getResultado() == 1d)||(ativ.getPredicao()==-1d && ativ.getResultado()==-1d)){
				paramA++;
			}
			else if(ativ.getPredicao() == 0d && (ativ.getResultado() == 1d)||ativ.getResultado()==-1d){
				paramB++;
			}
			else if((ativ.getPredicao()==1d||ativ.getPredicao()==-1d) && ativ.getResultado() == 0d){
				paramC++;
			}
			else if(ativ.getPredicao() == 0d && ativ.getResultado() == 0d){
				paramD++;
			}
		}

		return String.valueOf(calcularKma(paramA, paramB, paramC, paramD));
	}

	/**
	 * Metodo responsavel por realizar o calculo do kma
	 * @param paramA
	 * @param paramB
	 * @param paramC
	 * @param paramD
	 * @return kma
	 */
	private float calcularKma(float paramA, float paramB, float paramC, float paramD){
		return ((paramA + paramD) - (paramB + paramC)) / (paramA + paramB + paramC + paramD);
	}

	/**
	 * Metodo responsavel por realizar o calculo do KMB de uma atividade
	 * @throws Exception 
	 */
	@POST
	@Path("getKmb")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	private String getKmb (String ativStr) throws Exception{
		Atividade ativ = null;
		AtividadeDao adao = null;
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().serializeNulls().create();

		ativ = gson.fromJson(ativStr, Atividade.class);

		ativ = getTimes(ativ, ativStr);

		calcularKmb(ativ);

		adao = adao.getInstancia();

		return adao.atualizar(ativ)?"S":"N";

	}

	/**
	 * Metodo responsavel por retornar o valor kmb da atividade
	 * @param atividade
	 * @return kmb
	 */
	private void calcularKmb (Atividade ativ){
		if(ativ.getPredicao() == -1 && ativ.getResultado() == -1){
			ativ.setKmb(0d);
		}
		else if(ativ.getPredicao() == -1d && ativ.getResultado() == 0d){ 
			ativ.setKmb(0.5d);
		}
		else if(ativ.getPredicao() == -1d && ativ.getResultado() == 1d){ 
			ativ.setKmb(1d);
		}
		else if(ativ.getPredicao() == 0d && ativ.getResultado() == -1d){ 
			ativ.setKmb(-0.5d);
		}
		else if(ativ.getPredicao() == 0d && ativ.getResultado() == 0d){ 
			ativ.setKmb(0d);
		}
		else if(ativ.getPredicao() == 0d && ativ.getResultado() == 1d){ 
			ativ.setKmb(0.5d);
		}
		else if(ativ.getPredicao() == 1d && ativ.getResultado() == -1d){ 
			ativ.setKmb(-1d);
		}
		else if(ativ.getPredicao() == 1d && ativ.getResultado() == 0d){ 
			ativ.setKmb(-0.5d);
		}
		else if(ativ.getPredicao() == 1d && ativ.getResultado() == 1d){ 
			ativ.setKmb(0d);
		}
	}

	/**
	 * Metodo responsavel por consultar o nível do usuário
	 * @param Json String (Usuario)
	 * @return Float kma
	 * @throws Exception
	 */
	@POST
	@Path("getKmbMedio")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getKmbMedio(String usuarioStr){
		Usuario usuario = null;
		AtividadeDao adao = null;
		Gson gson = new Gson();

		usuario = gson.fromJson(usuarioStr, Usuario.class);

		adao = adao.getInstancia();

		return adao.consultarKmbMedio(usuario);
	}


	/**
	 * Metodo responsavel por consultar o kma médio
	 * @param Json String (Usuario)
	 * @return Float kma
	 * @throws Exception
	 * 
	 * 
	 * INCORRECT(-1, R.string.incorrect),
        P_CORRECT(0, R.string.partially_correct),
        CORRECT(1, R.string.correct);
	 * 
	 */
	@POST
	@Path("setKmaKmb")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String setKmaKmb(String ativString) throws Exception{
		Atividade ativ = null;
		AtividadeDao adao = null;
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().serializeNulls().create();

		ativ = gson.fromJson(ativString, Atividade.class);
		ativ = getTimes(ativ, ativString);

		adao = adao.getInstancia();

		if(ativ.getPredicao() == -1d && ativ.getResultado() == -1d){
			ativ.setKma(1d);//resultado incorreto, predição incorreta
			ativ.setKmb(0d);
		}
		else if(ativ.getPredicao() == 0d && ativ.getResultado() == -1d){ 
			ativ.setKma(-0.5d);//resultado incorreto, predição parcial
			ativ.setKmb(0.5d);
		}
		else if(ativ.getPredicao() == 1d && ativ.getResultado() == -1d){ 
			ativ.setKma(-1d);//resultado incorreto, predição correta
			ativ.setKmb(1d);
		}
		else if(ativ.getPredicao() == -1d && ativ.getResultado() == 0d){ 
			ativ.setKma(-0.5d);//resultado parcial, predição incorreta
			ativ.setKmb(-0.5d);
		}
		else if(ativ.getPredicao() == 0d && ativ.getResultado() == 0d){ 
			ativ.setKma(1d);//resultado parcial, predição parcial
			ativ.setKmb(0d);
		}
		else if(ativ.getPredicao() == 1d && ativ.getResultado() == 0d){ 
			ativ.setKma(-0.5d);//resultado parcial, predição correta
			ativ.setKmb(0.5d);
		}
		else if(ativ.getPredicao() == 1d && ativ.getResultado() == 1d){ 
			ativ.setKma(1d);
			ativ.setKmb(0d);
		}
		else if(ativ.getPredicao() == 0d && ativ.getResultado() == 1d){ 
			ativ.setKma(-0.5d);
			ativ.setKmb(-0.5d);
		}
		else if(ativ.getPredicao() == -1d && ativ.getResultado() == 1d){ 
			ativ.setKma(-1d);
			ativ.setKmb(-1d);
		}
		return adao.atualizar(ativ)?"S":"N";
	}

	/**
	 * Metodo responsavel por consultar o nivel do usuario
	 * @param Json String (Usuario)
	 * @return int Nivel
	 * @throws Exception
	 */
	@POST
	@Path("getNivel")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getNivel(String usuarioStr){
		try{		
			ArrayList<Atividade> ativs;
			ativs = listarAtividades(usuarioStr);
			if(Float.valueOf(getKmaMedio(usuarioStr))==1){
				switch (ativs.size()){
				case 0:
					return "1";
				case 1:
				case 2:
					return "2";
				default:
					return "3";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
