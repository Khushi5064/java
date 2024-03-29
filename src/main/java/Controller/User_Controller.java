package Controller;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Model.User;
import services.Services;
import Dao.User_Dao;

/**
 * Servlet implementation class UserController
 */
@WebServlet("/User_Controller")
public class User_Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public User_Controller() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");
		System.out.println(action);
		if (action.equalsIgnoreCase("register")) {
			User u = new User();
			u.setName(request.getParameter("name"));
			u.setContact(Long.parseLong(request.getParameter("contact")));
			u.setAddress(request.getParameter("address"));
			u.setEmail(request.getParameter("email"));
			u.setPassword(request.getParameter("password"));
			System.out.println(u);
			new User_Dao().insertUser(u);
			request.setAttribute("msg", "data inserted");
			request.getRequestDispatcher("login.jsp").forward(request, response);
		}

		else if (action.equalsIgnoreCase("login")) {
			User u = new User();
			u.setEmail(request.getParameter("email"));
			u.setPassword(request.getParameter("password"));
			User u1 = new User_Dao().login(u);
			System.out.println(u1);
			if (u1 == null) {
				request.setAttribute("validate", "email or password is inccoret");
				request.getRequestDispatcher("login.jsp").forward(request, response);
			} else {
				HttpSession session = request.getSession();
				session.setAttribute("data", u1);
				System.out.println("login done");
				request.getRequestDispatcher("home.jsp").forward(request, response);
			}
		} else if (action.equalsIgnoreCase("edit")) {
			int id = Integer.parseInt(request.getParameter("id"));
			User u = new User();
			request.setAttribute("data", u);
			request.getRequestDispatcher("update.jsp").forward(request, response);
		} else if (action.equalsIgnoreCase("update")) {
			User u = new User();
			u.setId(Integer.parseInt(request.getParameter("id")));
			u.setName(request.getParameter("name"));
			u.setContact(Long.parseLong(request.getParameter("contact")));
			u.setAddress(request.getParameter("address"));
			u.setEmail(request.getParameter("email"));
			u.setPassword(request.getParameter("password"));
			User u1 = new User_Dao().updateUser(u);
			HttpSession session = request.getSession();
			session.setAttribute("data", u1);
			request.getRequestDispatcher("home.jsp").forward(request, response);
		} else if (action.equalsIgnoreCase("delete")) {
			int id = Integer.parseInt(request.getParameter("id"));
			User u = new User_Dao().deleteUser(id);
			response.sendRedirect("home.jsp");
		} else if (action.equalsIgnoreCase("change password")) {
			int id = Integer.parseInt(request.getParameter("id"));
			String op = request.getParameter("op");
			String np = request.getParameter("np");
			String cnp = request.getParameter("cnp");
			boolean flag = new User_Dao().check01Password(id, op);
			System.out.println(flag);
			if (flag == true) {
				if (np.equals(cnp)) {
					new User_Dao().updatePassword(cnp, id);
					response.sendRedirect("index.jsp");
				} else {
					request.setAttribute("msg1", "new password and confirm new password not matched");
					request.getRequestDispatcher("change-password.jsp").forward(request, response);
				}
			} else {
				request.setAttribute("msg", "old password is not correct");
				request.getRequestDispatcher("change-password.jsp").forward(request, response);
			}
			System.out.println(op);
			System.out.println(np);
			System.out.println(cnp);
		} 
		else if (action.equalsIgnoreCase("get otp")) {
			String email = request.getParameter("email");
			boolean flag = new User_Dao().checkEmail(email);
			System.out.println(flag);
			if (flag == true) {
				Services s = new Services();
				Random r = new Random();
				int num = r.nextInt(9999);
				System.out.println(num);
				s.sendMail(email, num);
				System.out.println(email + num);
				request.setAttribute("email", email);
				request.setAttribute("otp", num);
				request.getRequestDispatcher("verify-otp.jsp").forward(request, response);
			}
			  else { request.setAttribute("msg", "email id not registerd");
			  request.getRequestDispatcher("forgot-password.jsp").forward(request, response); }
			 
		}
	}
}