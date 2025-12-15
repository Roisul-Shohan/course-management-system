<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="auth-include.jsp" %>

<%
    if (!"admin".equals(role)) {
        response.sendRedirect("signin.jsp");
        return;
    }
%>

                        <!DOCTYPE html>
                        <html lang="en">

                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Admin Dashboard - Course Management System</title>
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
                                            <h1 class="text-xl font-bold text-blue-300">COURSE-MANAGEMENT-SYSTEM</h1>
                                        </a>
                                    </div>
                                    <div class="flex items-center space-x-4">
                                        <a href="/signout"
                                            class="px-4 py-2 bg-red-600 hover:bg-red-700 rounded-md transition-colors inline-block text-white no-underline">
                                            Sign Out
                                        </a>
                                    </div>
                                </div>
                            </header>

                            <div class="flex">
                                <!-- Sidebar -->
                                <aside id="sidebar"
                                    class="sidebar-transition w-64 bg-slate-800/70 backdrop-blur-sm h-screen fixed left-0 top-16 pt-6 pb-8 border-r border-slate-700 overflow-y-auto">
                                    <div class="px-6 mb-8">
                                        <h2 class="text-xl font-bold text-blue-300 mb-6">Admin Panel</h2>
                                        <nav class="space-y-2">
                                             <a href="#" class="sidebar-link block px-4 py-3 rounded-lg text-gray-300 hover:bg-slate-700 hover:text-white transition-colors" data-section="dashboard">
                                                 <svg xmlns="http://www.w3.org/2000/svg" class="inline mr-2 h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                     <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2H5a2 2 0 00-2-2z" />
                                                     <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 5a2 2 0 012-2h4a2 2 0 012 2v2H8V5z" />
                                                 </svg>
                                                 Dashboard
                                             </a>
                                            <a href="#"
                                                class="sidebar-link block px-4 py-3 rounded-lg text-gray-300 hover:bg-slate-700 hover:text-white transition-colors"
                                                data-section="students">
                                                <svg xmlns="http://www.w3.org/2000/svg" class="inline mr-2 h-5 w-5"
                                                    fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                    <path stroke-linecap="round" stroke-linejoin="round"
                                                        stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0z" />
                                                    <path stroke-linecap="round" stroke-linejoin="round"
                                                        stroke-width="2"
                                                        d="M12 14a4 4 0 014 4h0a4 4 0 01-4 4h0a4 4 0 01-4-4h0a4 4 0 014-4z" />
                                                </svg>
                                                All Students
                                            </a>
                                            <a href="#"
                                                class="sidebar-link block px-4 py-3 rounded-lg text-gray-300 hover:bg-slate-700 hover:text-white transition-colors"
                                                data-section="teachers">
                                                <svg xmlns="http://www.w3.org/2000/svg" class="inline mr-2 h-5 w-5"
                                                    fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                    <path stroke-linecap="round" stroke-linejoin="round"
                                                        stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0z" />
                                                    <path stroke-linecap="round" stroke-linejoin="round"
                                                        stroke-width="2"
                                                        d="M12 14a4 4 0 014 4h0a4 4 0 01-4 4h0a4 4 0 01-4-4h0a4 4 0 014-4z" />
                                                </svg>
                                                All Teachers
                                            </a>
                                            <a href="#"
                                                class="sidebar-link block px-4 py-3 rounded-lg text-gray-300 hover:bg-slate-700 hover:text-white transition-colors"
                                                data-section="courses">
                                                <svg xmlns="http://www.w3.org/2000/svg" class="inline mr-2 h-5 w-5"
                                                    fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                    <path stroke-linecap="round" stroke-linejoin="round"
                                                        stroke-width="2"
                                                        d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 8h6v6H7V8z" />
                                                </svg>
                                                All Courses
                                            </a>
                                            <a href="#"
                                                class="sidebar-link block px-4 py-3 rounded-lg text-gray-300 hover:bg-slate-700 hover:text-white transition-colors"
                                                data-section="add-course">
                                                <svg xmlns="http://www.w3.org/2000/svg" class="inline mr-2 h-5 w-5"
                                                    fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                    <path stroke-linecap="round" stroke-linejoin="round"
                                                        stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                                                </svg>
                                                Add Course
                                            </a>
                                            <a href="#"
                                                class="sidebar-link block px-4 py-3 rounded-lg text-gray-300 hover:bg-slate-700 hover:text-white transition-colors"
                                                data-section="assign-teacher">
                                                <svg xmlns="http://www.w3.org/2000/svg" class="inline mr-2 h-5 w-5"
                                                    fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                    <path stroke-linecap="round" stroke-linejoin="round"
                                                        stroke-width="2"
                                                        d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                                                </svg>
                                                Assign Teacher
                                            </a>
                                        </nav>
                                    </div>

                                </aside>

                                <!-- Main Content -->
                                <main class="ml-64 flex-1 p-6">
                                    <div id="main-content">
                                      
                                        <div id="default-content">
                                            <!-- Welcome Section -->
                                            <div class="mb-8">
                                                <h1 class="text-3xl font-bold text-blue-300 mb-2">Welcome, Admin!</h1>
                                                <p class="text-gray-400">Manage your educational platform with ease</p>
                                            </div>

                                            <!-- Stats Cards -->
                                            <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                                                <div
                                                    class="bg-slate-800/50 backdrop-blur-sm rounded-xl p-6 border border-blue-500/20">
                                                    <div class="flex items-center">
                                                        <div
                                                            class="w-12 h-12 bg-blue-500 rounded-lg flex items-center justify-center">
                                                            <svg xmlns="http://www.w3.org/2000/svg"
                                                                class="h-6 w-6 text-white" fill="none"
                                                                viewBox="0 0 24 24" stroke="currentColor">
                                                                <path stroke-linecap="round" stroke-linejoin="round"
                                                                    stroke-width="2"
                                                                    d="M16 7a4 4 0 11-8 0 4 4 0 018 0z" />
                                                                <path stroke-linecap="round" stroke-linejoin="round"
                                                                    stroke-width="2"
                                                                    d="M12 14a4 4 0 014 4h0a4 4 0 01-4 4h0a4 4 0 01-4-4h0a4 4 0 014-4z" />
                                                            </svg>
                                                        </div>
                                                        <div class="ml-4">
                                                            <h3 class="text-sm font-medium text-gray-400">Total Students
                                                            </h3>
                                                            <p class="text-2xl font-bold" id="total-students">Loading...
                                                            </p>
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
                                                                <path stroke-linecap="round" stroke-linejoin="round"
                                                                    stroke-width="2"
                                                                    d="M16 7a4 4 0 11-8 0 4 4 0 018 0z" />
                                                                <path stroke-linecap="round" stroke-linejoin="round"
                                                                    stroke-width="2"
                                                                    d="M12 14a4 4 0 014 4h0a4 4 0 01-4 4h0a4 4 0 01-4-4h0a4 4 0 014-4z" />
                                                            </svg>
                                                        </div>
                                                        <div class="ml-4">
                                                            <h3 class="text-sm font-medium text-gray-400">Total Teachers
                                                            </h3>
                                                            <p class="text-2xl font-bold" id="total-teachers">Loading...
                                                            </p>
                                                        </div>
                                                    </div>
                                                </div>

                                                <div
                                                    class="bg-slate-800/50 backdrop-blur-sm rounded-xl p-6 border border-blue-500/20">
                                                    <div class="flex items-center">
                                                        <div
                                                            class="w-12 h-12 bg-purple-500 rounded-lg flex items-center justify-center">
                                                            <svg xmlns="http://www.w3.org/2000/svg"
                                                                class="h-6 w-6 text-white" fill="none"
                                                                viewBox="0 0 24 24" stroke="currentColor">
                                                                <path stroke-linecap="round" stroke-linejoin="round"
                                                                    stroke-width="2"
                                                                    d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 8h6v6H7V8z" />
                                                            </svg>
                                                        </div>
                                                        <div class="ml-4">
                                                            <h3 class="text-sm font-medium text-gray-400">Total Courses
                                                            </h3>
                                                            <p class="text-2xl font-bold" id="total-courses">Loading...
                                                            </p>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>

                                        </div>

                                        <!-- Students Content -->
                                        <div id="students-content" class="hidden">
                                            <h2 class="text-xl font-bold mb-4">All Students</h2>
                                            <div id="students-list" class="space-y-2">
                                               
                                            </div>
                                        </div>

                                        <!-- Teachers Content -->
                                        <div id="teachers-content" class="hidden">
                                            <h2 class="text-xl font-bold mb-4">All Teachers</h2>
                                            <div id="teachers-list" class="space-y-2">
                                              
                                            </div>
                                        </div>

                                        <!-- Courses Content -->
                                        <div id="courses-content" class="hidden">
                                            <h2 class="text-xl font-bold mb-4">All Courses</h2>
                                            <div id="courses-list" class="space-y-2">
                                               
                                            </div>
                                        </div>

                                        <!-- Add Course Content -->
                                        <div id="add-course-content" class="hidden">
                                            <h2 class="text-xl font-bold mb-4">Add New Course</h2>
                                            <form id="add-course-form" class="space-y-4">
                                                <input type="text" name="courseName" placeholder="Course Name"
                                                    class="w-full px-4 py-2 bg-slate-700 rounded-lg text-white"
                                                    required>
                                                <input type="text" name="courseCode" placeholder="Course Code"
                                                    class="w-full px-4 py-2 bg-slate-700 rounded-lg text-white"
                                                    required>
                                              
                                                <button type="submit"
                                                    class="px-4 py-2 bg-blue-600 hover:bg-blue-700 rounded-lg">Add
                                                    Course</button>
                                            </form>
                                        </div>

                                        <!-- Assign Teacher Content -->
                                        <div id="assign-teacher-content" class="hidden">
                                            <h2 class="text-xl font-bold mb-4">Assign Teacher to Course</h2>
                                            <form id="assign-teacher-form" class="space-y-4">
                                                <select name="courseId" id="assign-course-select"
                                                    class="w-full px-4 py-2 bg-slate-700 rounded-lg text-white"
                                                    required>
                                                    <option value="">Select Course</option>
                                                   
                                                </select>
                                                <select name="teacherId" id="assign-teacher-select"
                                                    class="w-full px-4 py-2 bg-slate-700 rounded-lg text-white"
                                                    required>
                                                    <option value="">Select Teacher</option>
                                                  
                                                </select>
                                                <button type="submit"
                                                    class="px-4 py-2 bg-blue-600 hover:bg-blue-700 rounded-lg">Assign
                                                    Teacher</button>
                                            </form>
                                        </div>

                                        <!-- Course Students Content -->
                                        <div id="course-students-content" class="hidden">
                                            <div class="flex justify-between items-center mb-8">
                                                <div class="flex items-center gap-3">
                                                    <div class="w-8 h-8 bg-green-500/20 rounded-lg flex items-center justify-center">
                                                        <svg class="w-5 h-5 text-green-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                                                        </svg>
                                                    </div>
                                                    <div>
                                                        <h2 class="text-2xl font-bold text-white">Course Students</h2>
                                                        <p class="text-sm text-gray-400">Students enrolled in this course</p>
                                                    </div>
                                                </div>
                                                <button id="back-to-courses-admin"
                                                    class="px-6 py-3 bg-slate-700 hover:bg-slate-600 rounded-lg transition-colors text-gray-300 hover:text-white border border-slate-600 flex items-center gap-2">
                                                    <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                                                    </svg>
                                                    Back to Courses
                                                </button>
                                            </div>

                                            <!-- Student Count -->
                                            <div class="mb-6">
                                                <div class="bg-gradient-to-r from-slate-800/50 to-slate-700/50 backdrop-blur-sm rounded-xl p-4 border border-slate-600/30">
                                                    <div class="flex items-center justify-center gap-2 text-sm text-gray-400">
                                                        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                                                        </svg>
                                                        <span id="student-count-admin">0</span> students enrolled
                                                    </div>
                                                </div>
                                            </div>

                                            <div id="course-students-list" class="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                                                <!-- Students will be loaded here -->
                                            </div>
                                        </div>
                                    </div>
                                </main>
                            </div>

                            <!-- Student Modal -->
                            <div id="student-modal"
                                class="fixed inset-0 bg-black bg-opacity-50 hidden flex items-center justify-center z-50">
                                <div class="bg-slate-800 rounded-lg p-6 w-full max-w-md mx-4">
                                    <div class="flex justify-between items-center mb-4">
                                        <h3 class="text-xl font-bold text-blue-300">Student Details</h3>
                                        <button id="close-modal" class="text-gray-400 hover:text-white">
                                            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none"
                                                viewBox="0 0 24 24" stroke="currentColor">
                                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                                    d="M6 18L18 6M6 6l12 12" />
                                            </svg>
                                        </button>
                                    </div>
                                    <div class="space-y-4">
                                        <div>
                                            <label class="block text-sm font-medium text-gray-400">Full Name</label>
                                            <p id="modal-fullname" class="text-white"></p>
                                        </div>
                                        <div>
                                            <label class="block text-sm font-medium text-gray-400">Email</label>
                                            <p id="modal-email" class="text-white"></p>
                                        </div>
                                        <div>
                                            <label class="block text-sm font-medium text-gray-400">Courses</label>
                                            <p id="modal-courses" class="text-white"></p>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- Teacher Modal -->
                            <div id="teacher-modal"
                                class="fixed inset-0 bg-black bg-opacity-50 hidden flex items-center justify-center z-50">
                                <div class="bg-slate-800 rounded-lg p-6 w-full max-w-md mx-4">
                                    <div class="flex justify-between items-center mb-4">
                                        <h3 class="text-xl font-bold text-blue-300">Teacher Details</h3>
                                        <button id="close-teacher-modal" class="text-gray-400 hover:text-white">
                                            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none"
                                                viewBox="0 0 24 24" stroke="currentColor">
                                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                                    d="M6 18L18 6M6 6l12 12" />
                                            </svg>
                                        </button>
                                    </div>
                                    <div class="space-y-4">
                                        <div>
                                            <label class="block text-sm font-medium text-gray-400">Full Name</label>
                                            <p id="teacher-modal-fullname" class="text-white"></p>
                                        </div>
                                        <div>
                                            <label class="block text-sm font-medium text-gray-400">Email</label>
                                            <p id="teacher-modal-email" class="text-white"></p>
                                        </div>
                                        <div>
                                            <label class="block text-sm font-medium text-gray-400">Courses</label>
                                            <p id="teacher-modal-courses" class="text-white"></p>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- Course Modal -->
                            <div id="course-modal"
                                class="fixed inset-0 bg-black bg-opacity-50 hidden flex items-center justify-center z-50">
                                <div class="bg-slate-800 rounded-lg p-6 w-full max-w-md mx-4">
                                    <div class="flex justify-between items-center mb-4">
                                        <h3 class="text-xl font-bold text-blue-300">Course Details</h3>
                                        <button id="close-course-modal" class="text-gray-400 hover:text-white">
                                            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none"
                                                viewBox="0 0 24 24" stroke="currentColor">
                                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                                    d="M6 18L18 6M6 6l12 12" />
                                            </svg>
                                        </button>
                                    </div>
                                    <div class="space-y-4">
                                        <div>
                                            <label class="block text-sm font-medium text-gray-400">Course Name</label>
                                            <p id="course-modal-name" class="text-white"></p>
                                        </div>
                                        <div>
                                            <label class="block text-sm font-medium text-gray-400">Course Code</label>
                                            <p id="course-modal-code" class="text-white"></p>
                                        </div>
                                        <div>
                                            <label class="block text-sm font-medium text-gray-400">Teacher
                                                Assigned</label>
                                            <p id="course-modal-teacher" class="text-white"></p>
                                        </div>
                                        <div>
                                            <label class="block text-sm font-medium text-gray-400">Students
                                                Enrolled</label>
                                            <p id="course-modal-students" class="text-white"></p>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <script src="assets/js/admin-sidebar.js?v=2"></script>

                        </body>

                        </html>