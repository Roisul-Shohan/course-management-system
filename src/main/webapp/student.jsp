<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.auth0.jwt.JWT" %>
<%@ page import="com.auth0.jwt.algorithms.Algorithm" %>
<%@ page import="com.auth0.jwt.interfaces.DecodedJWT" %>
<%@ page import="io.github.cdimascio.dotenv.Dotenv" %>
<%@ page import="com.cms.model.Student" %>


<%
Cookie[] cookies = request.getCookies();
String token = null;

if (cookies != null) {
    for (Cookie c : cookies) {
        if ("jwt".equals(c.getName())) {
            token = c.getValue();
            break;
        }
    }
}

if (token == null) {
    response.sendRedirect("signin.jsp");
    return;
}

String username = "";
String role = "";

try {
    Dotenv dotenv = Dotenv.load();
    String SECRET = dotenv.get("JWT_SECRET");

    DecodedJWT decoded = JWT.require(Algorithm.HMAC256(SECRET))
                            .build()
                            .verify(token);

    username = decoded.getSubject();
    role = decoded.getClaim("role").asString();

    if (!"student".equals(role)) {
        response.sendRedirect("signin.jsp");
        return;
    }

} catch (Exception e) {
    response.sendRedirect("signin.jsp");
    return;
}
%>

<%
Student studentObj = (Student) session.getAttribute("student");
%>

                                <!DOCTYPE html>
                                <html lang="en">

                                <head>
                                    <meta charset="UTF-8">
                                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                    <title>Student Dashboard - Course Management System</title>
                                    <script src="https://cdn.tailwindcss.com"></script>
                                    <style>
                                        body {
                                            background: #0f172a;
                                            background-image:
                                                linear-gradient(90deg, rgba(30, 41, 59, 0.15) 1px, transparent 1px),
                                                linear-gradient(rgba(30, 41, 59, 0.15) 1px, transparent 1px);
                                            background-size: 40px 40px;
                                        }

                                        .grid-pattern {
                                            position: absolute;
                                            top: 0;
                                            left: 0;
                                            width: 100%;
                                            height: 100%;
                                            pointer-events: none;
                                            z-index: -1;
                                        }

                                        .grid-pattern::before {
                                            content: '';
                                            position: absolute;
                                            top: 0;
                                            left: 0;
                                            width: 100%;
                                            height: 100%;
                                            background-image:
                                                linear-gradient(90deg, rgba(30, 41, 59, 0.15) 1px, transparent 1px),
                                                linear-gradient(rgba(30, 41, 59, 0.15) 1px, transparent 1px);
                                            background-size: 40px 40px;
                                        }

                                        .sidebar-transition {
                                            transition: all 0.3s ease;
                                        }
                                    </style>
                                    <script>
                                        tailwind.config = {
                                            theme: {
                                                extend: {
                                                    colors: {
                                                        primary: {
                                                            500: '#3b82f6',
                                                            600: '#2563eb',
                                                            700: '#1d4ed8'
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    </script>
                                </head>

                                <body class="min-h-screen bg-slate-900 text-white relative">
                                    <!-- Grid Pattern Background -->
                                    <div class="grid-pattern"></div>

                                    <!-- Header -->
                                    <header
                                        class="bg-slate-900/80 backdrop-blur-sm border-b border-blue-500/20 sticky top-0 z-50">
                                        <div class="container mx-auto px-6 py-4 flex justify-between items-center">
                                            <div class="flex items-center space-x-2">
                                                <a href="index.jsp" class="flex items-center space-x-2 no-underline">
                                                    <div
                                                        class="w-10 h-10 bg-blue-500 rounded-full flex items-center justify-center">
                                                        <span class="text-white font-bold text-lg">CMS</span>
                                                    </div>
                                                    <h1 class="text-xl font-bold text-blue-300">COURSE-MANAGEMENT-SYSTEM
                                                    </h1>
                                                </a>
                                            </div>
                                            <div class="flex items-center space-x-4">
                                                <a href="/signout">
                                                    <button
                                                        class="px-4 py-2 bg-red-600 hover:bg-red-700 rounded-md transition-colors">
                                                        Sign Out
                                                    </button>
                                                </a>

                                            </div>
                                        </div>
                                    </header>

                                    <div class="flex">
                                        <!-- Sidebar -->
                                        <aside id="sidebar"
                                            class="sidebar-transition w-64 bg-slate-800/70 backdrop-blur-sm h-screen fixed left-0 top-16 pt-6 pb-8 border-r border-slate-700 overflow-y-auto">
                                            <div class="px-6 mb-8">
                                                <h2 class="text-xl font-bold text-blue-300 mb-6">Student Panel</h2>
                                                <nav class="space-y-2">
                                                    <a href="#"
                                                        class="sidebar-link block px-4 py-3 rounded-lg text-gray-300 hover:bg-slate-700 hover:text-white transition-colors"
                                                        data-section="my-courses">
                                                        <svg xmlns="http://www.w3.org/2000/svg"
                                                            class="inline mr-2 h-5 w-5" fill="none" viewBox="0 0 24 24"
                                                            stroke="currentColor">
                                                            <path stroke-linecap="round" stroke-linejoin="round"
                                                                stroke-width="2"
                                                                d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 8h6v6H7V8z" />
                                                        </svg>
                                                        My Courses
                                                    </a>
                                                    <a href="#" id="register-course-link"
                                                        class="sidebar-link block px-4 py-3 rounded-lg text-gray-300 hover:bg-slate-700 hover:text-white transition-colors"
                                                        data-section="register-course">
                                                        <svg xmlns="http://www.w3.org/2000/svg"
                                                            class="inline mr-2 h-5 w-5" fill="none" viewBox="0 0 24 24"
                                                            stroke="currentColor">
                                                            <path stroke-linecap="round" stroke-linejoin="round"
                                                                stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                                                        </svg>
                                                        Register New Course
                                                    </a>
                                                    <a href="#" id="edit-profile-link"
                                                        class="sidebar-link block px-4 py-3 rounded-lg text-gray-300 hover:bg-slate-700 hover:text-white transition-colors"
                                                        data-section="edit-profile">
                                                        <svg xmlns="http://www.w3.org/2000/svg"
                                                            class="inline mr-2 h-5 w-5" fill="none" viewBox="0 0 24 24"
                                                            stroke="currentColor">
                                                            <path stroke-linecap="round" stroke-linejoin="round"
                                                                stroke-width="2"
                                                                d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                                                        </svg>
                                                        Edit Profile
                                                    </a>

                                                </nav>
                                            </div>
                                        </aside>

                                        <!-- Main Content -->
                                        <main class="ml-64 flex-1 p-6" style="margin-left: 16rem;">
                                            <div id="main-content">
                                                <!-- Dashboard Content (Default) -->
                                                <div id="dashboard-content">
                                                    <!-- Welcome Section -->
                                                    <div class="mb-8">
                                                        <h1 class="text-3xl font-bold text-blue-300 mb-2">Welcome back,
                                                            <%= username %>!
                                                        </h1>
                                                        <p class="text-gray-400">Track your academic progress and manage
                                                            your
                                                            courses</p>
                                                    </div>

                                                    <!-- Stats Cards -->
                                                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
                                                        <div
                                                            class="bg-slate-800/50 backdrop-blur-sm rounded-xl p-6 border border-blue-500/20">
                                                            <div class="flex items-center">
                                                                <div
                                                                    class="w-12 h-12 bg-blue-500 rounded-lg flex items-center justify-center">
                                                                    <svg xmlns="http://www.w3.org/2000/svg"
                                                                        class="h-6 w-6 text-white" fill="none"
                                                                        viewBox="0 0 24 24" stroke="currentColor">
                                                                        <path stroke-linecap="round"
                                                                            stroke-linejoin="round" stroke-width="2"
                                                                            d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 8h6v6H7V8z" />
                                                                    </svg>
                                                                </div>
                                                                <div class="ml-4">
                                                                    <h3 class="text-sm font-medium text-gray-400">
                                                                        Enrolled
                                                                        Courses</h3>
                                                                    <p class="text-2xl font-bold" id="enrolled-courses">
                                                                        Loading...</p>
                                                                </div>
                                                            </div>
                                                        </div>

                                                        <div
                                                            class="bg-slate-800/50 backdrop-blur-sm rounded-xl p-6 border border-blue-500/20">
                                                            <div class="flex items-center">
                                                                <div
                                                                    class="w-12 h-12 bg-green-500 rounded-lg flex items-center justify-center">
                                                                    <svg xmlns="http://www.w3.org/2000/svg"
                                                                        class="h-6 w-6 text-white" fill="none"
                                                                        viewBox="0 0 24 24" stroke="currentColor">
                                                                        <path stroke-linecap="round"
                                                                            stroke-linejoin="round" stroke-width="2"
                                                                            d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                                                                    </svg>
                                                                </div>
                                                                <div class="ml-4">
                                                                    <h3 class="text-sm font-medium text-gray-400">
                                                                        Completed
                                                                        Courses</h3>
                                                                    <p class="text-2xl font-bold"
                                                                        id="completed-courses">
                                                                        Loading...</p>
                                                                </div>
                                                            </div>
                                                        </div>


                                                        <!-- Recent Activity -->
                                                        <div class="">
                                                            <div id="recent-activity-list" class="space-y-3">
                                                                <!-- Activities will be loaded here dynamically -->
                                                                <div>

                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

                                                <!-- My Courses Content -->
                                                <div id="my-courses-content" class="hidden">
                                                    <h2 class="text-xl font-bold mb-4">My Courses</h2>
                                                    <p class="text-gray-400">Your enrolled courses will be displayed
                                                        here.
                                                    </p>
                                                    <div id="my-courses-list" class="space-y-4">
                                                        <!-- Courses will be loaded here -->
                                                    </div>
                                                </div>

                                                <!-- Register New Course Content -->
                                                <div id="register-course-content" class="hidden">
                                                    <h2 class="text-xl font-bold mb-4">Register New Course</h2>
                                                    <select id="available-courses-dropdown"
                                                        class="w-full px-4 py-2 bg-slate-700 rounded-lg text-white mb-4">
                                                        <option value="">Select a course</option>
                                                        <!-- Options will be populated dynamically -->
                                                    </select>
                                                    <button id="register-course-btn"
                                                        class="px-6 py-2 bg-blue-600 hover:bg-blue-700 rounded-lg transition-colors">
                                                        Register Course
                                                    </button>
                                                </div>

                                                <!-- Edit Profile Content -->
                                                <div id="edit-profile-content" class="hidden">
                                                    <h2 class="text-xl font-bold mb-4">Edit Profile</h2>
                                                    <div id="profile-update-message" class="mb-4"></div>
                                                    <form id="edit-profile-form" class="space-y-4 max-w-md">
                                                        <div>
                                                            <label class="block text-sm font-medium mb-2">Full
                                                                Name</label>
                                                            <input type="text" id="fullname" name="fullname"
                                                                class="w-full px-4 py-2 bg-slate-700 rounded-lg text-white">
                                                        </div>
                                                        <div>
                                                            <label class="block text-sm font-medium mb-2">Email</label>
                                                            <input type="email" id="email" name="email"
                                                                class="w-full px-4 py-2 bg-slate-700 rounded-lg text-white">
                                                        </div>
                                                        <div>
                                                            <label
                                                                class="block text-sm font-medium mb-2">Username</label>
                                                            <input type="text" id="username" name="username"
                                                                class="w-full px-4 py-2 bg-slate-700 rounded-lg text-white">
                                                        </div>
                                                        <div>
                                                            <label class="block text-sm font-medium mb-2">Password
                                                                (leave
                                                                blank
                                                                to keep current)</label>
                                                            <input type="password" id="password" name="password"
                                                                class="w-full px-4 py-2 bg-slate-700 rounded-lg text-white">
                                                        </div>
                                                        <button type="submit" id="update-profile-btn"
                                                            class="px-6 py-2 bg-blue-600 hover:bg-blue-700 rounded-lg transition-colors">
                                                            Update Profile
                                                        </button>
                                                    </form>
                                                </div>
                                            </div>
                                        </main>
                                    </div>




                                    <script src="assets/js/student-sidebar.js?v=2"></script>



                                </body>

                                </html>