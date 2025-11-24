// Student sidebar functionality

// Sidebar navigation functionality
const sidebarLinks = document.querySelectorAll('.sidebar-link');
const contentSections = document.querySelectorAll('#main-content > div');
// ------------------- Register Course -------------------
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

            switch (this.getAttribute('data-section')) {
                case 'my-courses':
                    loadMyCourses();
                    break;
                case 'register-course':
                    loadAvailableCourses();
                    break;
                case 'edit-profile':
                    loadProfileData();
                    break;
            }
        } else {
            const defaultContent = document.getElementById('dashboard-content');
            if (defaultContent) defaultContent.classList.remove('hidden');
        }
    });
});

// Load student stats and activities on page load
window.addEventListener('load', function () {
    loadStudentStats();

});

// ------------------- Student Stats -------------------
function loadStudentStats() {
    document.getElementById('enrolled-courses').textContent = '0';
    document.getElementById('completed-courses').textContent = '0';
}

// ------------------- JWT Token -------------------
function getJwtToken() {
    const cookies = document.cookie.split(';');
    for (let cookie of cookies) {
        const [name, value] = cookie.trim().split('=');
        if (name === 'jwt') return value;
    }
    return null;
}

// ------------------- Edit Profile -------------------
function loadProfileData() {
    const form = document.getElementById('edit-profile-form');
    const messageDiv = document.getElementById('profile-update-message');
    messageDiv.innerHTML = '';

    fetch('StudentProfileServlet', {
        method: 'GET',
        credentials: 'same-origin', // send HttpOnly cookie automatically
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

        // Trim values to prevent empty or whitespace-only submission
        const fullname = document.getElementById('fullname').value.trim();
        const username = document.getElementById('username').value.trim();
        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value;


        // Local validation
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

        fetch('StudentUpdateProfileServlet', { // POST to update servlet
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
                    loadProfileData(); // reload fields with updated values
                    // Hide edit profile section and show dashboard
                    document.getElementById('edit-profile-content').classList.add('hidden');
                    document.getElementById('dashboard-content').classList.remove('hidden');
                    // Reset sidebar active state
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

// ------------------- My Courses -------------------
function loadMyCourses() {
    const coursesList = document.getElementById('my-courses-list');
    coursesList.innerHTML = '<p class="text-gray-400">Loading courses...</p>';

    fetch('StudentCoursesServlet', {
        method: 'GET',
        credentials: 'same-origin', // send HttpOnly cookie automatically
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

function displayCourses(courses) {
    const coursesList = document.getElementById('my-courses-list');
    if (courses.length === 0) {
        coursesList.innerHTML = '<p class="text-gray-400">No courses enrolled yet</p>';
        return;
    }

    let html = '';
    courses.forEach(course => {
        html += `
        <div class="bg-slate-700/50 rounded-lg p-4 border border-slate-600">
            <h3 class="text-lg font-semibold text-blue-300">${course.name}</h3>
            <p class="text-sm text-gray-400">Course Code: ${course.courseCode || 'N/A'}</p>
            <p class="text-sm text-gray-400">Teacher: ${course.teacherName || 'Not assigned'}</p>
            <p class="text-sm text-gray-400">Registered Students: ${course.studentCount}</p>
        </div>
        `;
    });

    coursesList.innerHTML = html;
}

