<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>COURSE-MANAGEMENT-SYSTEM - Streamline Your Educational Experience</title>
        <script src="https://cdn.tailwindcss.com"></script>
        <style>
            body {
                background-image:
                    linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
                    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
                background-size: 40px 40px;
                background-color: #0f0c14;
            }

            .gradient-text {
                background: linear-gradient(to right, #4F46E5, #3B82F6, #059669);
                -webkit-background-clip: text;
                background-clip: text;
                color: transparent;
            }

            .logo-glow {
                box-shadow: 0 0 20px rgba(79, 70, 229, 0.5);
                transition: all 0.3s ease;
            }

            .logo-glow:hover {
                box-shadow: 0 0 30px rgba(79, 70, 229, 0.8);
                transform: scale(1.05);
            }

            .cta-button:hover {
                transform: translateY(-2px);
                box-shadow: 0 6px 20px rgba(59, 130, 246, 0.6);
            }

            .hero h1 {
                font-size: 4rem;
                line-height: 1.1;
            }

            @media (min-width: 768px) {
                .hero h1 {
                    font-size: 6rem;
                }
            }
        </style>
    </head>

    <body class="bg-gray-900 text-white min-h-screen">
        <div class="container mx-auto px-4">
            <header class="flex justify-between items-center py-6 border-b border-gray-700">
                <div class="flex items-center gap-3">
                    <div
                        class="w-12 h-12 bg-gradient-to-br from-indigo-600 to-blue-500 rounded-full flex items-center justify-center logo-glow">
                        <span class="text-white font-bold text-xl">CMS</span>
                    </div>
                    <div class="text-2xl font-bold gradient-text">COURSE-MANAGEMENT-SYSTEM</div>
                </div>
                <a href="signin.jsp">
                    <button
                        class="px-4 py-2 bg-gray-800 hover:bg-gray-700 border border-gray-600 rounded-md transition-colors">
                        Sign In
                    </button>
                </a>

            </header>

            <main class="py-12">
                <section class="text-center py-15">
                    <h1 class="hero text-4xl md:text-6xl font-extrabold mb-6 leading-tight">
                        COURSE<span class="block gradient-text">MANAGEMENT</span><br>
                        <span class="text-gray-300  text-3xl md:text-5xl font-extrabold mb-6 leading-tight">Your
                            Complete Educational Platform</span>
                    </h1>
                    <p class="text-xl text-gray-300 max-w-3xl mx-auto mb-10">
                        Transform your educational institution with our powerful course management system designed for
                        modern learning environments.
                    </p>

                    <a href="signin.jsp">
                        <button
                            class="px-8 py-3 bg-gradient-to-r from-indigo-600 to-blue-500 hover:from-indigo-700 hover:to-blue-600 text-white font-semibold rounded-full text-lg transition-all cta-button">
                            Get Started
                        </button>
                    </a>

                </section>
            </main>


        </div>
    </body>

    </html>