<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Login - Course Management System</title>
        <script src="https://cdn.tailwindcss.com"></script>
        <style>
            body {
                background: #0f172a;
                background-image:
                    linear-gradient(90deg, rgba(30, 41, 59, 0.1) 1px, transparent 1px),
                    linear-gradient(rgba(30, 41, 59, 0.1) 1px, transparent 1px);
                background-size: 20px 20px;
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
                    linear-gradient(90deg, rgba(30, 41, 59, 0.1) 1px, transparent 1px),
                    linear-gradient(rgba(30, 41, 59, 0.1) 1px, transparent 1px);
                background-size: 20px 20px;
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

    <body class="min-h-screen bg-slate-900 text-white relative ">
        <!-- Grid Pattern Background -->
        <div class="grid-pattern"></div>

        <!-- Header -->
        <header class="bg-slate-900/80 backdrop-blur-sm border-b border-blue-500/20 sticky top-0 z-50">
            <div class="container mx-auto px-6 py-4 flex justify-between items-center">
                <div class="flex items-center space-x-2">
                    <a href="index.jsp" class="flex items-center space-x-2">
                        <div class="w-10 h-10 bg-blue-500 rounded-full flex items-center justify-center">
                            <span class="text-white font-bold text-lg">CMS</span>
                        </div>
                        <h1 class="text-xl font-bold text-blue-300">COURSE-MANAGEMENT-SYSTEM</h1>
                    </a>
                </div>

                <!-- Right side: Sign Up button -->
                <div class="flex items-center">
                    <a href="signup.jsp"
                        class="ml-4 inline-block px-4 py-2 bg-transparent border border-blue-500 text-blue-300 rounded hover:bg-blue-600 hover:text-white transition-colors">
                        Sign Up
                    </a>
                </div>

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

                <!-- Login Form -->
                <div class="bg-slate-800/50 backdrop-blur-sm rounded-xl p-8 shadow-2xl border border-blue-500/20">
                    <h3 class="text-2xl font-semibold mb-6 text-center">Sign In</h3>

                    <form class="space-y-6" method="post" action="signin">
                        <div>
                            <label for="username" class="block text-sm font-medium text-gray-300 mb-2">
                                Username
                            </label>
                            <input type="text" id="username" name="username"
                                class="w-full px-4 py-3 bg-slate-700 border border-slate-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-white placeholder-gray-400"
                                placeholder="Enter your username" required />
                        </div>

                        <div>
                            <label for="role" class="block text-sm font-medium text-gray-300 mb-2">
                                Role
                            </label>
                            <select id="role" name="role"
                                class="w-full px-4 py-3 bg-slate-700 border border-slate-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-white "
                                required>
                                <option value="admin">Admin</option>
                                <option value="teacher">Teacher</option>
                                <option value="student">Student</option>
                            </select>
                        </div>

                        <div>
                            <label for="password" class="block text-sm font-medium text-gray-300 mb-2">
                                Password
                            </label>
                            <input type="password" id="password" name="password"
                                class="w-full px-4 py-3 bg-slate-700 border border-slate-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-white placeholder-gray-400"
                                placeholder="Enter your password" required />
                        </div>

                        <div class="flex items-center justify-between">
                            <div class="flex items-center">
                                <input id="remember-me" type="checkbox"
                                    class="h-4 w-4 text-blue-600 focus:ring-2 focus:ring-blue-500 border-gray-300 rounded" />
                                <label for="remember-me" class="ml-2 block text-sm text-gray-300">
                                    Remember me
                                </label>
                            </div>
                            <a href="#" class="text-sm text-blue-400 hover:text-blue-300">
                                Forgot password?
                            </a>
                        </div>

                        <button type="submit"
                            class="w-full py-3 px-4 bg-blue-600 hover:bg-blue-700 rounded-lg font-medium transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 focus:ring-offset-slate-900">
                            Sign In
                        </button>
                    </form>

                    <div class="mt-6 text-center">
                        <p class="text-sm text-gray-400">
                            Don't have an account?
                            <a href="signup.jsp" class="text-blue-400 hover:text-blue-300 font-medium ml-1">
                                Create one
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