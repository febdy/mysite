package com.javaex.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.javaex.dao.BoardDao;
import com.javaex.util.WebUtil;
import com.javaex.vo.BoardVo;
import com.javaex.vo.UserVo;

@WebServlet("/board")
public class BoardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String actionName = request.getParameter("a");
		String url;
		
		if("list".equals(actionName)) {
			BoardDao boardDao = new BoardDao();
			List<BoardVo> bList = boardDao.getList();
			
			request.setAttribute("bList", bList);
			url = "/WEB-INF/views/board/list.jsp";
			WebUtil.forward(request, response, url);

		} else if("writeform".equals(actionName)) {
			url = "/WEB-INF/views/board/write.jsp";
			WebUtil.forward(request, response, url);

		} else if("write".equals(actionName)) {
			HttpSession session = request.getSession();
			UserVo authUser = (UserVo) session.getAttribute("authUser");
			
			String title = request.getParameter("title");
			String content = request.getParameter("content");
			
			BoardVo boardVo = new BoardVo();
			boardVo.setWriterNo(authUser.getNo());
			boardVo.setTitle(title);
			boardVo.setWriter(authUser.getName());
			boardVo.setViewCount(0);
			boardVo.setContent(content);
			
			BoardDao boardDao = new BoardDao();
			boardDao.write(boardVo);
			
			url = "board?a=list";
			WebUtil.redirect(request, response, url);
			
		} else if("view".equals(actionName)) {
			int no = Integer.valueOf(request.getParameter("no"));
			BoardDao boardDao = new BoardDao();
			BoardVo boardVo = boardDao.getArticle(no);
			boardDao.view(no, boardVo.getViewCount()+1);
			
			request.setAttribute("boardVo", boardVo);
			url = "/WEB-INF/views/board/view.jsp";
			WebUtil.forward(request, response, url);
			
		} else if("modifyform".equals(actionName)) {
			HttpSession session = request.getSession();
			UserVo authUser = (UserVo) session.getAttribute("authUser");

			int no = Integer.valueOf(request.getParameter("no"));
			BoardDao boardDao = new BoardDao();
			BoardVo boardVo = boardDao.getArticle(no);
			
			if((authUser != null) && (authUser.getNo() == boardVo.getWriterNo())) {
				request.setAttribute("boardVo", boardVo);
				url = "/WEB-INF/views/board/modify.jsp";				
				WebUtil.forward(request, response, url);
			} else {
				url = "board?a=list";
				WebUtil.redirect(request, response, url);
			}
			
		} else if("modify".equals(actionName)) {
			HttpSession session = request.getSession();
			UserVo authUser = (UserVo) session.getAttribute("authUser");
			
			int no = Integer.valueOf(request.getParameter("no"));
			BoardDao boardDao = new BoardDao();
			BoardVo boardVo = boardDao.getArticle(no);

			if((authUser != null) && (authUser.getNo() == boardVo.getWriterNo())) {
				String newTitle = request.getParameter("title");
				String newContent = request.getParameter("content");
				
				boardVo.setTitle(newTitle);
				boardVo.setContent(newContent);
				
				boardDao.modify(boardVo);
			
				url = "board?a=view&no=" + boardVo.getArticleNo();
			} else {
				url = "board?a=list";
			}
			
			WebUtil.redirect(request, response, url);			
			
		} else if("delete".equals(actionName)) {
			HttpSession session = request.getSession();
			UserVo authUser = (UserVo) session.getAttribute("authUser");
			int no = Integer.parseInt(request.getParameter("no"));
			int writerNo = Integer.parseInt(request.getParameter("writerno"));
			
			if((authUser != null) && (authUser.getNo() == writerNo)) {
				BoardDao boardDao = new BoardDao();
				boardDao.delete(no);
				
			} else {
				
			}

			url = "board?a=list";
			WebUtil.redirect(request, response, url);
			
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
