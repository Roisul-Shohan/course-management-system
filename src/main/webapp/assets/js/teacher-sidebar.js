
const sidebarLinks = document.querySelectorAll('.sidebar-link');
const contentSections = document.querySelectorAll('#main-content > div');

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
                case 'courses':
                    loadAllStudents();
                    break;
                case 'edit-profile':
                    loadTeacherProfileData();
                    break;
                case 'dashboard':
                 
                    break;
            }
        } else {
            const defaultContent = document.getElementById('dashboard-content');
            if (defaultContent) defaultContent.classList.remove('hidden');
        }
    });
});

window.addEventListener('load', function () {
    loadTeacherStats();
});


function loadTeacherStats() {

    fetch('TeacherCoursesServlet', {
        method: 'GET',
        credentials: 'same-origin',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            document.getElementById('courses-taught').textContent = data.courses.length;
            let totalStudents = 0;
            data.courses.forEach(course => {
                totalStudents += course.studentCount || 0;
            });
            document.getElementById('students-enrolled').textContent = totalStudents;
        } else {
            document.getElementById('courses-taught').textContent = '0';
            document.getElementById('students-enrolled').textContent = '0';
        }
    })
    .catch(error => {
        console.error('Error loading teacher stats:', error);
        document.getElementById('courses-taught').textContent = 'Error';
        document.getElementById('students-enrolled').textContent = 'Error';
    });
}
function loadAllStudents() {
    document.getElementById('courses-content').classList.add('hidden');
    document.getElementById('course-students-content').classList.remove('hidden');

    const studentsList = document.getElementById('course-students-list');
    studentsList.innerHTML = '<p class="text-gray-400">Loading students...</p>';

    fetch('TeacherCoursesServlet', {
        method: 'GET',
        credentials: 'same-origin',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            const courses = data.courses;
            const studentMap = new Map();
            const promises = courses.map(course =>
                fetch('TeacherCourseStudentsServlet?courseId=' + course.id, {
                    method: 'GET',
                    credentials: 'same-origin',
                    headers: { 'Content-Type': 'application/json' }
                })
                .then(response => response.json())
                .then(studentData => {
                    if (studentData.success) {
                        studentData.students.forEach(student => {
                            studentMap.set(student.id, student);
                        });
                    }
                })
                .catch(error => {
                    console.error('Error loading students for course ' + course.id + ':', error);
                })
            );
            Promise.all(promises).then(() => {
                const allStudents = Array.from(studentMap.values());
                displayCourseStudents(allStudents);
                document.getElementById('student-count').textContent = allStudents.length;
            });
        } else {
            studentsList.innerHTML = '<p class="text-red-400">' + (data.message || 'Failed to load courses') + '</p>';
        }
    })
    .catch(error => {
        console.error('Error loading courses:', error);
        studentsList.innerHTML = '<p class="text-red-400">Error loading courses</p>';
    });
}





function displayCourseStudents(students) {
    const studentsList = document.getElementById('course-students-list');
    if (students.length === 0) {
        studentsList.innerHTML = `
            <div class="text-center py-12">
                <svg class="mx-auto h-24 w-24 text-gray-400 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
                <h3 class="text-lg font-medium text-gray-300 mb-2">No students enrolled</h3>
                <p class="text-gray-400">This course doesn't have any enrolled students yet.</p>
            </div>
        `;
        return;
    }

    let html = '';
    students.forEach(student => {
        html += `
        <div class="bg-slate-800/60 backdrop-blur-sm rounded-xl p-5 border border-slate-600/50 hover:border-green-500/30 transition-all duration-300 hover:shadow-lg hover:shadow-green-500/10">
            <div class="flex items-center gap-4">
                <div class="w-12 h-12 bg-gradient-to-br from-green-500 to-green-600 rounded-full flex items-center justify-center">
                    <svg class="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                    </svg>
                </div>

                <div class="flex-1">
                    <h3 class="text-lg font-bold text-white">${student.fullname}</h3>
                    <div class="flex items-center gap-4 mt-1">
                        <span class="text-sm text-gray-400 flex items-center gap-1">
                            <svg class="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 12a4 4 0 10-8 0 4 4 0 008 0zm0 0v1.5a2.5 2.5 0 005 0V12a9 9 0 10-9 9m4.5-1.206a8.959 8.959 0 01-4.5 1.207" />
                            </svg>
                            ${student.username || 'N/A'}
                        </span>
                        <span class="text-sm text-gray-400 flex items-center gap-1">
                            <svg class="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 4.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                            </svg>
                            ${student.email || 'N/A'}
                        </span>
                    </div>
                </div>

                <div class="flex items-center gap-2">
                    <div class="w-3 h-3 bg-green-500 rounded-full animate-pulse"></div>
                    <span class="text-sm text-green-400 font-medium">Active</span>
                </div>
            </div>
        </div>
        `;
    });

    studentsList.innerHTML = html;
}

document.getElementById('back-to-courses').addEventListener('click', function() {
    document.getElementById('course-students-content').classList.add('hidden');
    document.getElementById('courses-content').classList.remove('hidden');
});


function loadTeacherProfileData() {
    const form = document.getElementById('edit-profile-form');

    fetch('TeacherProfileServlet', {
        method: 'GET',
        credentials: 'same-origin',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            document.querySelector('input[name="fullname"]').value = data.profile.fullname || '';
            document.querySelector('input[name="username"]').value = data.profile.username || '';
            document.querySelector('input[name="email"]').value = data.profile.email || '';
        } else {
            alert('Error loading profile: ' + (data.message || 'Unknown error'));
        }
    })
    .catch(err => {
        console.error(err);
        alert('Error loading profile');
    });

    form.onsubmit = function (e) {
        e.preventDefault();

        const fullname = document.querySelector('input[name="fullname"]').value.trim();
        const username = document.querySelector('input[name="username"]').value.trim();
        const email = document.querySelector('input[name="email"]').value.trim();
        const password = document.querySelector('input[name="password"]').value;

        if (!fullname || !username || !email) {
            alert('Fullname, username, and email are required');
            return;
        }

        const params = new URLSearchParams();
        params.append('fullname', fullname);
        params.append('username', username);
        params.append('email', email);
        params.append('password', password);

        fetch('TeacherProfileServlet', {
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
                document.getElementById('edit-profile-content').classList.add('hidden');
                document.getElementById('dashboard-content').classList.remove('hidden');

                sidebarLinks.forEach(link => {
                    link.classList.remove('bg-blue-600', 'text-white');
                    link.classList.add('text-gray-300');
                });
            } else {
                alert('Error: ' + (data.message || 'Failed to update profile'));
            }
        })
        .catch(err => {
            console.error(err);
            alert('Error updating profile');
        });
    };
}