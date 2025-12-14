<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="auth-include.jsp" %>

<%
    if (!"teacher".equals(role)) {
        response.sendRedirect("signin.jsp");
        return;
    }

    com.cms.dao.TeacherDAO teacherDAO = new com.cms.dao.TeacherDAO();
    com.cms.model.Teacher teacher = teacherDAO.findByUsername(username);
%>

                        <!DOCTYPE html>
                        <html lang="en">

                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Teacher Dashboard - Course Management System</title>
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

                                        <a href="/signout">
                                            <button
                                                class="px-4 py-2 bg-red-600 hover:bg-red-700 rounded-md transition-colors">
                                                Sign Out
                                            </button></a>

                                    </div>
                                </div>
                            </header>

                            <div class="flex">
                                <!-- Sidebar -->
                                <aside id="sidebar"
                                    class="sidebar-transition w-64 bg-slate-800/70 backdrop-blur-sm h-screen fixed left-0 top-16 pt-6 pb-8 border-r border-slate-700 overflow-y-auto">
                                    <div class="px-6 mb-8">
                                        <h2 class="text-xl font-bold text-blue-300 mb-6">Teacher Panel</h2>
                                        <nav class="space-y-2">
                                            <a href="#" class="sidebar-link group block px-4 py-4 rounded-xl text-gray-300 hover:bg-gradient-to-r hover:from-blue-500/20 hover:to-purple-500/20 hover:text-white transition-all duration-300 border border-transparent hover:border-blue-500/30" data-section="dashboard">
                                                <div class="flex items-center gap-3">
                                                    <div class="w-8 h-8 bg-slate-700/50 group-hover:bg-blue-500/20 rounded-lg flex items-center justify-center transition-colors duration-300">
                                                        <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2H5a2 2 0 00-2-2z" />
                                                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 5a2 2 0 012-2h4a2 2 0 012 2v2H8V5z" />
                                                        </svg>
                                                    </div>
                                                    <span class="font-medium">Dashboard</span>
                                                </div>
                                            </a>
                                            <a href="javascript:void(0)"
                                                class="sidebar-link block px-4 py-3 rounded-lg text-gray-300 hover:bg-slate-700 hover:text-white transition-colors"
                                                data-section="courses">
                                                <svg xmlns="http://www.w3.org/2000/svg" class="inline mr-2 h-5 w-5"
                                                    fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                    <path stroke-linecap="round" stroke-linejoin="round"
                                                        stroke-width="2"
                                                        d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 8h6v6H7V8z" />
                                                </svg>
                                                <span class="sidebar-text">Courses</span>
                                            </a>
                                            <a href="#"
                                                class="sidebar-link block px-4 py-3 rounded-lg text-gray-300 hover:bg-slate-700 hover:text-white transition-colors"
                                                data-section="edit-profile">
                                                <svg xmlns="http://www.w3.org/2000/svg" class="inline mr-2 h-5 w-5"
                                                    fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                                    <path stroke-linecap="round" stroke-linejoin="round"
                                                        stroke-width="2"
                                                        d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                                                </svg>
                                                <span class="sidebar-text">Edit Profile</span>
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
                                                <h1 class="text-3xl font-bold text-blue-300 mb-2">Welcome <%= username %>!</h1>
                                                <p class="text-gray-400">Manage your courses and update your profile</p>
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
                                                                <path stroke-linecap="round" stroke-linejoin="round"
                                                                    stroke-width="2"
                                                                    d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 8h6v6H7V8z" />
                                                            </svg>
                                                        </div>
                                                        <div class="ml-4">
                                                            <h3 class="text-sm font-medium text-gray-400">Courses Taught
                                                            </h3>
                                                            <p class="text-2xl font-bold" id="courses-taught">
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
                                                                <path stroke-linecap="round" stroke-linejoin="round"
                                                                    stroke-width="2"
                                                                    d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                                                            </svg>
                                                        </div>
                                                        <div class="ml-4">
                                                            <h3 class="text-sm font-medium text-gray-400">Students
                                                                Enrolled</h3>
                                                            <p class="text-2xl font-bold" id="students-enrolled">
                                                                Loading...</p>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>

                                            <!-- Recent Activity -->
                                            <div>
                                                <div id="recent-activity-list" class="space-y-3">
                                                </div>
                                            </div>
                                        </div>

                                        <!-- Courses Content -->
                                        <div id="courses-content" class="hidden">
                                            <h2 class="text-xl font-bold mb-4">My Courses</h2>
                                            <div id="courses-list" class="space-y-4">
                                                <!-- Courses will be loaded here dynamically -->
                                            </div>
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
                                                <button id="back-to-courses"
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
                                                        <span id="student-count">0</span> students enrolled
                                                    </div>
                                                </div>
                                            </div>

                                            <div id="course-students-list" class="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                                                <!-- Students will be loaded here -->
                                            </div>
                                        </div>


                                        <!-- Edit Profile Content -->
                                        <div id="edit-profile-content" class="hidden">
                                            <h2 class="text-xl font-bold mb-4">Edit Profile</h2>
                                            <form id="edit-profile-form" class="space-y-4 max-w-md">
                                                <div>
                                                    <label class="block text-sm font-medium mb-2">Full Name</label>
                                                    <input type="text" name="fullname"
                                                        class="w-full px-4 py-2 bg-slate-700 rounded-lg text-white"
                                                        required>
                                                </div>
                                                <div>
                                                    <label class="block text-sm font-medium mb-2">Email</label>
                                                    <input type="email" name="email"
                                                        class="w-full px-4 py-2 bg-slate-700 rounded-lg text-white"
                                                        required>
                                                </div>
                                                <div>
                                                    <label class="block text-sm font-medium mb-2">Username</label>
                                                    <input type="text" name="username"
                                                        class="w-full px-4 py-2 bg-slate-700 rounded-lg text-white"
                                                        required>
                                                </div>
                                                <div>
                                                    <label class="block text-sm font-medium mb-2">Password (leave blank to keep current)</label>
                                                    <input type="password" name="password"
                                                        class="w-full px-4 py-2 bg-slate-700 rounded-lg text-white">
                                                </div>
                                                <button type="submit"
                                                    class="px-6 py-2 bg-blue-600 hover:bg-blue-700 rounded-lg transition-colors">
                                                    Update Profile
                                                </button>
                                            </form>
                                        </div>

                                    </div>
                            </div>
                            </main>
                            </div>

                            <script>

                                // Sidebar navigation functionality
                                document.addEventListener('DOMContentLoaded', function () {
                                    const sidebarLinks = document.querySelectorAll('.sidebar-link');
                                    const contentSections = document.querySelectorAll('#main-content > div');


                                    sidebarLinks.forEach(link => {
                                        link.addEventListener('click', function (e) {
                                            e.preventDefault();

                                            // Remove active class from all links
                                            sidebarLinks.forEach(l => l.classList.remove('bg-blue-600', 'text-white'));
                                            sidebarLinks.forEach(l => l.classList.add('text-gray-300'));

                                            // Add active class to clicked link
                                            this.classList.add('bg-blue-600', 'text-white');
                                            this.classList.remove('text-gray-300');

                                            // Hide all content sections
                                            contentSections.forEach(section => section.classList.add('hidden'));

                                            // Show corresponding content
                                            const sectionId = this.getAttribute('data-section') + '-content';
                                            const targetSection = document.getElementById(sectionId);
                                            if (targetSection) {
                                                targetSection.classList.remove('hidden');
                                                // Load content for the section
                                                if (this.getAttribute('data-section') === 'courses') {
                                                    loadCourses();
                                                } else if (this.getAttribute('data-section') === 'edit-profile') {
                                                    loadProfileData();
                                                }
                                            } else {
                                                // Fallback to default if section not found
                                                document.getElementById('dashboard-content').classList.remove('hidden');
                                            }
                                        });
                                    });
                                });


                                // Load teacher stats and activities on page load
                                window.addEventListener('load', function () {
                                    loadTeacherStats();
                                    loadCourses();
                                    loadProfileData();
                                });

                                // Load teacher stats
                                function loadTeacherStats() {
                                    fetch('TeacherCoursesServlet')
                                        .then(response => response.json())
                                        .then(data => {
                                            if (data.success) {
                                                document.getElementById('courses-taught').textContent = data.courses.length;
                                                let totalStudents = 0;
                                                data.courses.forEach(course => {
                                                    totalStudents += (course.studentCount || 0);
                                                });
                                                document.getElementById('students-enrolled').textContent = totalStudents;
                                            } else {
                                                console.error('Failed to load teacher stats:', data.message);
                                                document.getElementById('courses-taught').textContent = '0';
                                                document.getElementById('students-enrolled').textContent = '0';
                                            }
                                        })
                                        .catch(error => {
                                            console.error('Error loading teacher stats:', error);
                                            document.getElementById('courses-taught').textContent = '0';
                                            document.getElementById('students-enrolled').textContent = '0';
                                        });
                                }

                                // Load courses
                                function loadCourses() {
                                    fetch('TeacherCoursesServlet')
                                        .then(response => {
                                            return response.text();
                                        })
                                        .then(rawText => {
                                            const data = JSON.parse(rawText);
                                            if (data.success) {
                                                const coursesList = document.getElementById('courses-list');
                                                coursesList.innerHTML = '';

                                                if (data.courses.length === 0) {
                                                    coursesList.innerHTML = '<p class="text-gray-400">No courses assigned yet.</p>';
                                                } else {
                                                    data.courses.forEach(course => {
                                                        const courseCard = document.createElement('div');
                                                        courseCard.className = 'bg-slate-800/50 backdrop-blur-sm rounded-xl p-6 border border-blue-500/20';

                                                        // Create elements individually to avoid template issues
                                                        const courseName = course.name || 'N/A';
                                                        const courseCode = course.courseCode || 'N/A';
                                                        const studentCount = course.studentCount || 0;
                                                        const courseId = course.id;


                                                        courseCard.innerHTML = '<div class="flex justify-between items-start text-white">' +
                                                            '<div>' +
                                                            '<h3 class="text-lg font-bold text-blue-300">' + courseName + '</h3>' +
                                                            '<p class="text-gray-300">Course Code: <span class="text-white">' + courseCode + '</span></p>' +
                                                            '<p class="text-gray-300">Students: <span class="text-white">' + studentCount + '</span></p>' +
                                                            '</div>' +
                                                            '<button class="show-students-btn px-4 py-2 bg-blue-600 hover:bg-blue-700 rounded-lg transition-colors" data-course-id="' + courseId + '">' +
                                                            'Show Students' +
                                                            '</button>' +
                                                            '</div>';
                                                        coursesList.appendChild(courseCard);
                                                    });

                                                    // Add event listeners to show students buttons
                                                    document.querySelectorAll('.show-students-btn').forEach(btn => {
                                                        btn.addEventListener('click', function () {
                                                            const courseId = this.getAttribute('data-course-id');
                                                            if (!courseId) {
                                                                console.error('Course ID is missing from button!');
                                                                return;
                                                            }
                                                            showCourseStudents(courseId);
                                                        });
                                                    });
                                                }
                                            } else {
                                                document.getElementById('courses-list').innerHTML = '<p class="text-red-400">Failed to load courses.</p>';
                                            }
                                        })
                                        .catch(error => {
                                            console.error('Error loading courses:', error);
                                            document.getElementById('courses-list').innerHTML = '<p class="text-red-400">Error loading courses.</p>';
                                        });
                                }

                                // Show course students
                                function showCourseStudents(courseId) {
                                    // Hide courses content and show students content
                                    document.getElementById('courses-content').classList.add('hidden');
                                    document.getElementById('course-students-content').classList.remove('hidden');

                                    // Load students for this course
                                    const fetchUrl = 'TeacherCourseStudentsServlet?courseId=' + courseId;
                                    fetch(fetchUrl)
                                        .then(response => {
                                            return response.json();
                                        })
                                        .then(data => {
                                            if (data.success) {
                                                const students = data.students || [];
                                                renderStudents(students);

                                                // Update student count
                                                document.getElementById('student-count').textContent = students.length;
                                            } else {
                                                console.error('Failed to load students:', data.message);
                                                document.getElementById('course-students-list').innerHTML = '<div class="col-span-full text-center py-12"><div class="bg-red-500/10 border border-red-500/20 rounded-xl p-6"><p class="text-red-400 font-medium">Failed to load students.</p></div></div>';
                                                document.getElementById('student-count').textContent = '0';
                                            }
                                        })
                                        .catch(error => {
                                            console.error('Error loading students:', error);
                                            document.getElementById('course-students-list').innerHTML = '<div class="col-span-full text-center py-12"><div class="bg-red-500/10 border border-red-500/20 rounded-xl p-6"><p class="text-red-400 font-medium">Error loading students.</p></div></div>';
                                            document.getElementById('student-count').textContent = '0';
                                        });
                                }

                                // Render students list
                                function renderStudents(students) {
                                    const studentsList = document.getElementById('course-students-list');
                                    studentsList.innerHTML = '';

                                    if (students.length === 0) {
                                        studentsList.innerHTML = '<div class="col-span-full text-center py-12"><div class="bg-slate-800/30 border border-slate-600/30 rounded-xl p-8"><svg class="w-16 h-16 text-gray-500 mx-auto mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" /></svg><h3 class="text-lg font-medium text-gray-400 mb-2">No students enrolled</h3><p class="text-gray-500">This course has no enrolled students yet.</p></div></div>';
                                        return;
                                    }

                                    students.forEach(student => {
                                        const studentCard = document.createElement('div');
                                        studentCard.className = 'bg-gradient-to-br from-slate-800/60 to-slate-700/60 backdrop-blur-sm rounded-xl p-6 border border-slate-600/30 hover:border-blue-500/40 transition-all duration-300 hover:shadow-lg hover:shadow-blue-500/10 group';

                                        // Safely get student data with fallbacks
                                        const fullname = student.fullname || 'Unknown Student';
                                        const username = student.username || 'N/A';
                                        const email = student.email || 'N/A';

                                        const initials = fullname !== 'Unknown Student' ? fullname.split(' ').map(n => n[0]).join('').toUpperCase().substring(0, 2) : '??';
                                        const avatarColor = ['bg-blue-500', 'bg-green-500', 'bg-purple-500', 'bg-pink-500', 'bg-indigo-500'][Math.floor(Math.random() * 5)];

                                        studentCard.innerHTML = '<div class="flex items-start space-x-4">' +
                                            '<div class="w-12 h-12 ' + avatarColor + ' rounded-xl flex items-center justify-center flex-shrink-0 group-hover:scale-110 transition-transform duration-300">' +
                                                '<span class="text-white font-bold text-sm">' + initials + '</span>' +
                                            '</div>' +
                                            '<div class="flex-1 min-w-0">' +
                                                '<h3 class="text-lg font-bold text-white mb-1 truncate group-hover:text-blue-300 transition-colors">' + fullname + '</h3>' +
                                                '<div class="space-y-1">' +
                                                    '<div class="flex items-center text-sm text-gray-400">' +
                                                        '<svg class="w-4 h-4 mr-2 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">' +
                                                            '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />' +
                                                        '</svg>' +
                                                        '<span class="truncate">' + username + '</span>' +
                                                    '</div>' +
                                                    '<div class="flex items-center text-sm text-gray-400">' +
                                                        '<svg class="w-4 h-4 mr-2 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">' +
                                                            '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 4.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />' +
                                                        '</svg>' +
                                                        '<span class="truncate">' + email + '</span>' +
                                                    '</div>' +
                                                '</div>' +
                                            '</div>' +
                                        '</div>';
                                        studentsList.appendChild(studentCard);
                                    });
                                }

                                // Back to courses button
                                document.getElementById('back-to-courses').addEventListener('click', function () {
                                    document.getElementById('course-students-content').classList.add('hidden');
                                    document.getElementById('courses-content').classList.remove('hidden');
                                });

                                // Load profile data
                                function loadProfileData() {
                                    fetch('TeacherProfileServlet')
                                        .then(response => response.json())
                                        .then(data => {
                                            if (data.success) {
                                                document.querySelector('#edit-profile-form input[name="fullname"]').value = data.profile.fullname;
                                                document.querySelector('#edit-profile-form input[name="username"]').value = data.profile.username;
                                                document.querySelector('#edit-profile-form input[name="email"]').value = data.profile.email;
                                            } else {
                                                console.error('Failed to load profile data:', data.message);
                                            }
                                        })
                                        .catch(error => {
                                            console.error('Error loading profile data:', error);
                                        });
                                }


                                // Handle profile form submission
                                document.getElementById('edit-profile-form').addEventListener('submit', function (e) {
                                    e.preventDefault();

                                    const params = new URLSearchParams();
                                    params.append('fullname', document.querySelector('#edit-profile-form input[name="fullname"]').value);
                                    params.append('username', document.querySelector('#edit-profile-form input[name="username"]').value);
                                    params.append('email', document.querySelector('#edit-profile-form input[name="email"]').value);
                                    params.append('password', document.querySelector('#edit-profile-form input[name="password"]').value);

                                    fetch('/TeacherProfileServlet', {
                                        method: 'POST',
                                        headers: {
                                            'Content-Type': 'application/x-www-form-urlencoded'
                                        },
                                        body: params.toString()
                                    })
                                        .then(response => response.json())
                                        .then(data => {
                                            if (data.success) {
                                                alert('Profile updated successfully!');
                                                // Reload profile data
                                                loadProfileData();
                                                // Hide edit profile section and show dashboard
                                                document.getElementById('edit-profile-content').classList.add('hidden');
                                                document.getElementById('dashboard-content').classList.remove('hidden');
                                                // Reset sidebar active state
                                                document.querySelectorAll('.sidebar-link').forEach(link => {
                                                    link.classList.remove('bg-blue-600', 'text-white');
                                                    link.classList.add('text-gray-300');
                                                });
                                            } else {
                                                alert('Failed to update profile: ' + data.message);
                                            }
                                        })
                                        .catch(error => {
                                            console.error('Error updating profile:', error);
                                            alert('Error updating profile. Please try again.');
                                        });
                                });


    </script>
</body>

</html>