const sidebarLinks = document.querySelectorAll('.sidebar-link');

const contentSections = document.querySelectorAll('#main-content > div');

window.addEventListener('load', function () {
    loadStudentStats();

});

function loadStudentStats() {
    const enrolledCoursesElement = document.getElementById('enrolled-courses');

    fetch('StudentCoursesServlet', {
        method: 'GET',
        credentials: 'same-origin',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            if (enrolledCoursesElement) {
                enrolledCoursesElement.textContent = data.courses.length;
            } else {
                console.error('Enrolled courses element not found');
            }
        } else {
            if (enrolledCoursesElement) {
                enrolledCoursesElement.textContent = '0';
            } else {
                console.error('Enrolled courses element not found');
            }
        }
    })
    .catch(error => {
        console.error('Error loading enrolled courses count:', error);
        if (enrolledCoursesElement) {
            enrolledCoursesElement.textContent = '0';
        } else {
            console.error('Enrolled courses element not found');
        }
    });
}


function loadAvailableCourses() {
    const dropdown = document.getElementById('available-courses-dropdown');
    dropdown.innerHTML = '<option value="">Loading courses...</option>';

    fetch('StudentAvailableCoursesServlet', {
        method: 'GET',
        credentials: 'same-origin',
        headers: { 'Content-Type': 'application/json' }
    })
        .then(response => {
            return response.json();
        })
        .then(courses => {
            dropdown.innerHTML = '<option value="">Select a course</option>';

            if (courses.length === 0) {
                dropdown.innerHTML = '<option value="">No courses available</option>';
                return;
            }

            courses.forEach(course => {
                const option = document.createElement('option');
                option.value = course.id;
                option.textContent = `${course.name} (${course.courseCode})`;
                dropdown.appendChild(option);
            });
        })
        .catch(error => {
            console.error('Error loading available courses:', error);
            dropdown.innerHTML = '<option value="">Error loading courses</option>';
        });

    // Handle course registration
    const registerBtn = document.getElementById('register-course-btn');
    registerBtn.onclick = function () {
        const selectedCourseId = dropdown.value;
        if (!selectedCourseId) {
            alert('Please select a course to register');
            return;
        }

        const params = new URLSearchParams();
        params.append('courseId', selectedCourseId);

        fetch('StudentRegisterCourseServlet', {
            method: 'POST',
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: params.toString()
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('Successfully registered for the course!');
                    // Refresh available courses
                    loadAvailableCourses();
                    // Optionally refresh enrolled courses count
                    loadStudentStats();
                } else {
                    alert('Error: ' + (data.error || 'Failed to register for course'));
                }
            })
            .catch(error => {
                console.error('Error registering for course:', error);
                alert('Error registering for course. Please try again.');
            });
    };
}

sidebarLinks.forEach(link => {
    link.addEventListener('click', function (e) {
        e.preventDefault();

        sidebarLinks.forEach(l => l.classList.remove('bg-blue-600', 'text-white'));
        sidebarLinks.forEach(l => l.classList.add('text-gray-300'));

        this.classList.add('bg-blue-600', 'text-white');
        this.classList.remove('text-gray-300');

        contentSections.forEach(section => section.classList.add('hidden'));

        const sectionId = this.getAttribute('data-section') + '-content';
        const targetSection = document.getElementById(sectionId);
        if (targetSection) {
            targetSection.classList.remove('hidden');

            switch (this.getAttribute('data-section')) {
                case 'my-courses':
                    loadMyCourses();
                    break;
                case 'register-course':
                    loadAvailableCourses();
                    break;
                case 'available-courses':
                    loadAvailableCoursesList();
                    break;
                case 'edit-profile':
                    loadProfileData();
                    break;
                case 'dashboard':
                    const defaultContent = document.getElementById('dashboard-content');
                    if (defaultContent) defaultContent.classList.remove('hidden');
                    break;
            }
        } else {
            const defaultContent = document.getElementById('dashboard-content');
            if (defaultContent) defaultContent.classList.remove('hidden');
        }
    });
});


function loadProfileData() {
    const form = document.getElementById('edit-profile-form');
    const messageDiv = document.getElementById('profile-update-message');
    messageDiv.innerHTML = '';

    fetch('StudentProfileServlet', {
        method: 'GET',
        credentials: 'same-origin',
        headers: { 'Content-Type': 'application/json' }
    })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                document.getElementById('fullname').value = data.profile.fullname || '';
                document.getElementById('username').value = data.profile.username || '';
                document.getElementById('email').value = data.profile.email || '';
            } else {
                messageDiv.innerHTML = `<p class="text-red-400">${data.message}</p>`;
            }
        })
        .catch(err => {
            console.error(err);
            messageDiv.innerHTML = `<p class="text-red-400">${err.message}</p>`;
        });

    form.onsubmit = function (e) {
        e.preventDefault();
        messageDiv.innerHTML = '<p class="text-blue-400">Updating profile...</p>';

        const fullname = document.getElementById('fullname').value.trim();
        const username = document.getElementById('username').value.trim();
        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value;


        if (!fullname) {
            messageDiv.innerHTML = '<p class="text-red-400">Fullname is required</p>';
            return;
        }
        if (!username) {
            messageDiv.innerHTML = '<p class="text-red-400">Username is required</p>';
            return;
        }
        if (!email) {
            messageDiv.innerHTML = '<p class="text-red-400">Email is required</p>';
            return;
        }

        const params = new URLSearchParams();
        params.append('fullname', fullname);
        params.append('username', username);
        params.append('email', email);
        params.append('password', password);

        fetch('StudentUpdateProfileServlet', {
            method: 'POST',
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: params.toString()
        })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    alert('Profile updated successfully!');
                    messageDiv.innerHTML = '<p class="text-green-400">Profile updated successfully!</p>';
                    loadProfileData(); 

                    document.getElementById('edit-profile-content').classList.add('hidden');
                    document.getElementById('dashboard-content').classList.remove('hidden');
                
                    sidebarLinks.forEach(link => {
                        link.classList.remove('bg-blue-600', 'text-white');
                        link.classList.add('text-gray-300');
                    });
                } else {
                    messageDiv.innerHTML = `<p class="text-red-400">${data.message}</p>`;
                }
            })
            .catch(err => {
                console.error(err);
                messageDiv.innerHTML = `<p class="text-red-400">${err.message}</p>`;
            });
    };
}

function loadMyCourses() {
    const coursesList = document.getElementById('my-courses-list');
    coursesList.innerHTML = '<p class="text-gray-400">Loading courses...</p>';

    fetch('StudentCoursesServlet', {
        method: 'GET',
        credentials: 'same-origin', 
        headers: { 'Content-Type': 'application/json' }
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) displayCourses(data.courses);
            else coursesList.innerHTML = '<p class="text-red-400">' + (data.message || 'Failed to load courses') + '</p>';
        })
        .catch(error => {
            console.error('Error loading courses:', error);
            coursesList.innerHTML = '<p class="text-red-400">Error loading courses</p>';
        });
}

function loadAvailableCoursesList() {
    const coursesList = document.getElementById('available-courses-list');
    coursesList.innerHTML = '<p class="text-gray-400">Loading courses...</p>';

    fetch('StudentAvailableCoursesServlet', {
        method: 'GET',
        credentials: 'same-origin',
        headers: { 'Content-Type': 'application/json' }
    })
        .then(response => response.json())
        .then(data => {
            if (data.length === 0) {
                coursesList.innerHTML = `
                    <div class="text-center py-16">
                        <div class="w-32 h-32 mx-auto mb-6 bg-slate-800/50 rounded-full flex items-center justify-center">
                            <svg class="w-16 h-16 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                            </svg>
                        </div>
                        <h3 class="text-2xl font-bold text-white mb-3">No Available Courses</h3>
                        <p class="text-gray-400 text-lg">You are already enrolled in all available courses.</p>
                    </div>
                `;
                return;
            }
            displayAvailableCourses(data);
        })
        .catch(error => {
            console.error('Error loading available courses:', error);
            coursesList.innerHTML = '<p class="text-red-400">Error loading courses</p>';
        });
}

function displayAvailableCourses(courses) {
    const colors = ['blue', 'green', 'purple', 'orange', 'pink'];
    let html = '';
    courses.forEach((course, index) => {
        const color = colors[index % colors.length];
        const teacherName = course.teacherName || 'Not assigned';
        const studentCount = course.studentCount || 0;

        html += `
        <div class="bg-slate-800/60 backdrop-blur-sm rounded-2xl p-6 border border-slate-600/40 hover:border-${color}-500/60 transition-all duration-300 hover:shadow-xl hover:shadow-${color}-500/20 group">
            <div class="flex items-center gap-4 mb-4">
                <div class="w-12 h-12 bg-${color}-500/20 rounded-xl flex items-center justify-center group-hover:bg-${color}-500/30 transition-colors">
                    <svg class="w-6 h-6 text-${color}-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                    </svg>
                </div>
                <div class="flex-1">
                    <h3 class="text-xl font-bold text-white group-hover:text-${color}-300 transition-colors">${course.name}</h3>
                    <p class="text-${color}-300 font-medium text-sm">${course.courseCode || 'N/A'}</p>
                </div>
            </div>
            <div class="flex justify-between items-center mb-4">
                <div class="text-sm text-gray-400">
                    <span class="font-medium">Instructor:</span> <span class="text-white ml-1">${teacherName}</span>
                </div>
                <div class="text-sm text-gray-400">
                    <span class="font-medium">Students:</span> <span class="text-white ml-1">${studentCount}</span>
                </div>
            </div>
            <div class="flex justify-start">
                <button class="px-6 py-2 bg-green-600 hover:bg-green-700 rounded-lg transition-colors text-white font-medium enroll-btn" data-course-id="${course.id}">
                    Enroll
                </button>
            </div>
        </div>
        `;
    });

    const coursesList = document.getElementById('available-courses-list');
    coursesList.innerHTML = html;

    // Add event listeners
    document.querySelectorAll('.enroll-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const courseId = this.getAttribute('data-course-id');
            enrollInCourse(courseId);
        });
    });
}

function enrollInCourse(courseId) {
    const params = new URLSearchParams();
    params.append('courseId', courseId);

    fetch('StudentRegisterCourseServlet', {
        method: 'POST',
        credentials: 'same-origin',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: params.toString()
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('Successfully enrolled in the course!');
            // Refresh the list
            loadAvailableCoursesList();
            // Update enrolled courses count
            loadStudentStats();
        } else {
            alert('Error: ' + (data.error || 'Failed to enroll'));
        }
    })
    .catch(error => {
        console.error('Error enrolling:', error);
        alert('Error enrolling in course. Please try again.');
    });
}

function displayCourses(courses) {
    const coursesList = document.getElementById('my-courses-list');
    if (courses.length === 0) {
        coursesList.innerHTML = `
            <div class="text-center py-16">
                <div class="w-32 h-32 mx-auto mb-6 bg-slate-800/50 rounded-full flex items-center justify-center">
                    <svg class="w-16 h-16 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                    </svg>
                </div>
                <h3 class="text-2xl font-bold text-white mb-3">No Courses Yet</h3>
                <p class="text-gray-400 text-lg">You haven't enrolled in any courses yet.</p>
                <p class="text-blue-400 text-sm mt-2 font-medium">Visit the "Register New Course" section to get started</p>
            </div>
        `;
        return;
    }

    // Color schemes for different courses
    const colors = ['blue', 'green', 'purple', 'orange', 'pink'];
    let html = '';
    courses.forEach((course, index) => {
        const color = colors[index % colors.length];
        const teacherName = course.teacherName || 'Not assigned';
        const studentCount = course.studentCount || 0;

        html += `
        <div class="bg-slate-800/60 backdrop-blur-sm rounded-2xl p-6 border border-slate-600/40 hover:border-${color}-500/60 transition-all duration-300 hover:shadow-xl hover:shadow-${color}-500/20 group">
            <div class="flex items-center gap-4 mb-4">
                <div class="w-12 h-12 bg-${color}-500/20 rounded-xl flex items-center justify-center group-hover:bg-${color}-500/30 transition-colors">
                    <svg class="w-6 h-6 text-${color}-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                    </svg>
                </div>
                <div class="flex-1">
                    <h3 class="text-xl font-bold text-white group-hover:text-${color}-300 transition-colors">${course.name}</h3>
                    <p class="text-${color}-300 font-medium text-sm">${course.courseCode || 'N/A'}</p>
                </div>
            </div>
            <div class="space-y-2 mb-4">
                <div class="text-sm text-gray-400">
                    <span class="font-medium">Instructor:</span> <span class="text-white">${teacherName}</span>
                </div>
                <div class="text-sm text-gray-400">
                    <span class="font-medium">Students:</span> <span class="text-white">${studentCount}</span>
                </div>
            </div>
            <div class="flex items-center justify-between">
                <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-green-500/20 text-green-400 border border-green-500/30">
                    <div class="w-2 h-2 bg-green-400 rounded-full mr-2 animate-pulse"></div>
                    Enrolled
                </span>
                <div class="text-${color}-400 text-sm font-medium">Active</div>
            </div>
        </div>
        `;
    });

    coursesList.innerHTML = html;
}

