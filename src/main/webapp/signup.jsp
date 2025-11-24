<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Sign Up - Course Management System</title>
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
        <header class="bg-slate-900/80 backdrop-blur-sm border-b border-blue-500/20 sticky top-0 z-50">
            <div class="container mx-auto px-6 py-4 flex justify-between items-center">
                <div class="flex items-center space-x-2">
                    <div class="w-10 h-10 bg-blue-500 rounded-full flex items-center justify-center">
                        <span class="text-white font-bold text-lg">CMS</span>
                    </div>
                    <h1 class="text-xl font-bold text-blue-300">COURSE-MANAGEMENT-SYSTEM</h1>
                </div>
                <a href="signin.jsp">
                    <button class="px-4 py-2 bg-blue-600 hover:bg-blue-700 rounded-md transition-colors">
                        Sign In
                    </button>
                </a>
            </div>
        </header>

        <!-- Main Content -->
        <main class="container mx-auto px-6 py-12">
            <div class="max-w-md mx-auto">
                <!-- Hero Section -->
                <div class="text-center mb-12">
                    <h2 class="text-4xl md:text-5xl font-bold mb-4">
                        <span class="block text-white">COURSE</span>
                        <span class="block text-blue-400">MANAGEMENT</span>
                    </h2>
                    <p class="text-xl text-gray-300 mt-6">
                        Your Complete Educational Platform
                    </p>
                    <p class="text-gray-400 mt-4 max-w-lg mx-auto">
                        Transform your educational institution with our powerful course management system designed for
                        modern learning environments.
                    </p>
                </div>

                <% String error=request.getParameter("error"); String msg="" ; if ("invalidrole".equals(error)) {
                    msg="Please select a valid role!" ; } else if ("usernameexists".equals(error)) {
                    msg="Username already exists!" ; } else if ("password".equals(error)) { msg="Password not matched!"
                    ; } else if (error !=null) { msg="Unknown error!" ; } %>

                    <!-- Sign Up Form -->
                    <div>
                        <form action="SignupServlet" method="post"
                            class="bg-slate-800 p-8 rounded-xl shadow-lg border border-slate-700 mt-10">

                            <h3 class="text-2xl font-bold text-blue-400 mb-6 text-center">Create Account</h3>

                            <% if (msg !=null && !msg.isEmpty()) { %>
                                <div class="bg-red-600 text-white p-3 rounded-md mb-4 text-center">
                                    <%= msg %>
                                </div>
                                <% } %>

                                    <div class="mb-4">
                                        <label class="block text-gray-300 mb-1">Full Name</label>
                                        <input type="text" name="fullname"
                                            class="w-full p-3 rounded bg-slate-700 text-white"
                                            placeholder="Enter full name" required>
                                    </div>

                                    <div class="mb-4">
                                        <label class="block text-gray-300 mb-1">Username</label>
                                        <input type="text" name="username"
                                            class="w-full p-3 rounded bg-slate-700 text-white"
                                            placeholder="Enter username" required>
                                    </div>

                                    <div class="mb-4">
                                        <label class="block text-gray-300 mb-1">Email</label>
                                        <input type="email" name="email"
                                            class="w-full p-3 rounded bg-slate-700 text-white" placeholder="Enter email"
                                            required>
                                    </div>

                                    <div class="mb-4">
                                        <label class="block text-gray-300 mb-1">Password</label>
                                        <input type="password" name="password"
                                            class="w-full p-3 rounded bg-slate-700 text-white"
                                            placeholder="Enter password" required>
                                    </div>

                                    <div class="mb-4">
                                        <label class="block text-gray-300 mb-1">Confirm Password</label>
                                        <input type="password" name="confirmpassword"
                                            class="w-full p-3 rounded bg-slate-700 text-white"
                                            placeholder="Confirm password" required>
                                    </div>

                                    <div class="mb-6">
                                        <label class="block text-gray-300 mb-1">Select Role</label>
                                        <select name="role" class="w-full p-3 rounded bg-slate-700 text-white" required>
                                            <option value="">-- Select Role --</option>
                                            <option value="student">Student</option>
                                            <option value="teacher">Teacher</option>
                                        </select>
                                    </div>

                                    <button type="submit"
                                        class="w-full bg-blue-600 hover:bg-blue-700 p-3 rounded text-white font-bold">
                                        Create Account
                                    </button>
                        </form>

                        <div class="mt-6 text-center">
                            <p class="text-sm text-gray-400">
                                Already have an account?
                                <a href="signin.jsp" class="text-blue-400 hover:text-blue-300 font-medium ml-1">
                                    Sign In
                                </a>
                            </p>
                        </div>
                    </div>
            </div>
        </main>

        <!-- Footer -->
        <footer class="mt-12 py-6 border-t border-slate-700">
            <div class="container mx-auto px-6 text-center text-gray-400 text-sm">
                &copy; 2025 Course Management System. All rights reserved.
            </div>
        </footer>
    </body>

    </html>