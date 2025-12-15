
function showSection(sectionId) {
    
    const section = document.getElementById(sectionId);
    if (section) {
        section.classList.remove('hidden');
    } else {
        document.getElementById('default-content').classList.remove('hidden');
    }
}

function loadStats() {
    fetch('/AdminStatsServlet?t=' + Date.now(), {
        method: 'GET',
        credentials: 'include'
    })
        .then(response => {
            if (response.status === 401 || response.status === 403) {
                alert('Unauthorized access. Redirecting to login.');
                window.location.href = 'signin.jsp';
                throw new Error('Unauthorized');
            }
            if (!response.ok) {
                throw new Error('HTTP error! status: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            const totalStudents = document.getElementById('total-students');
            const totalTeachers = document.getElementById('total-teachers');
            const totalCourses = document.getElementById('total-courses');

            if (data.stats) {
                if (totalStudents) {
                    totalStudents.textContent = data.stats.totalStudents || 0;
                } else {
                    console.error('loadStats: total-students element not found');
                }

                if (totalTeachers) {
                    totalTeachers.textContent = data.stats.totalTeachers || 0;
                } else {
                    console.error('loadStats: total-teachers element not found');
                }

                if (totalCourses) {
                    totalCourses.textContent = data.stats.totalCourses || 0;
                } else {
                    console.error('loadStats: total-courses element not found');
                }
            } else {
                console.error('loadStats: stats not found in data');
                if (totalStudents) totalStudents.textContent = 'N/A';
                if (totalTeachers) totalTeachers.textContent = 'N/A';
                if (totalCourses) totalCourses.textContent = 'N/A';
            }

        })
        .catch(error => {

            console.error('loadStats: Error occurred during stats loading:', error);
            console.error('loadStats: Error details:', error.message);

            const totalStudentsEl = document.getElementById('total-students');
            const totalTeachersEl = document.getElementById('total-teachers');
            const totalCoursesEl = document.getElementById('total-courses');

            if (totalStudentsEl) {
                totalStudentsEl.textContent = 'Error';
            }
            if (totalTeachersEl) {
                totalTeachersEl.textContent = 'Error';
            }
            if (totalCoursesEl) {
                totalCoursesEl.textContent = 'Error';
            }
        });
}

function initAdminSidebar() {
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('sidebar-link') || e.target.closest('.sidebar-link')) {
            const link = e.target.classList.contains('sidebar-link') ? e.target : e.target.closest('.sidebar-link');
            e.preventDefault();

            document.querySelectorAll('.sidebar-link').forEach(l => {
                l.classList.remove('bg-blue-600', 'text-white');
                l.classList.add('text-gray-300');
            });

            link.classList.add('bg-blue-600', 'text-white');
            link.classList.remove('text-gray-300');

            document.querySelectorAll('#main-content > div').forEach(div => div.classList.add('hidden'));

            const section = link.getAttribute('data-section');
            switch (section) {
                case 'students':
                    loadStudents();
                    showSection(section + '-content');
                    break;
                case 'teachers':
                    loadTeachers();
                    showSection(section + '-content');
                    break;
                case 'courses':
                    loadCourses();
                    showSection(section + '-content');
                    break;
                case 'assign-teacher':
                    loadCoursesForSelect();
                    loadTeachersForSelect();
                    showSection(section + '-content');
                    break;
                case 'add-course':
                    showSection(section + '-content');
                    break;
                case 'dashboard':
                    loadStats();
                    showSection('default-content');
                    break;
                default:
                    showSection('default-content');
                    break;
            }
        }
    });

    const modal = document.getElementById('student-modal');
    const closeModalBtn = document.getElementById('close-modal');

    if (closeModalBtn) {
        closeModalBtn.addEventListener('click', function () {
            modal.classList.add('hidden');
        });
    }

    if (modal) {
        modal.addEventListener('click', function (e) {
            if (e.target === modal) {
                modal.classList.add('hidden');
            }
        });
    }

    const teacherModal = document.getElementById('teacher-modal');
    const closeTeacherModalBtn = document.getElementById('close-teacher-modal');

    if (closeTeacherModalBtn) {
        closeTeacherModalBtn.addEventListener('click', function () {
            teacherModal.classList.add('hidden');
        });
    }

    if (teacherModal) {
        teacherModal.addEventListener('click', function (e) {
            if (e.target === teacherModal) {
                teacherModal.classList.add('hidden');
            }
        });
    }

    const courseModal = document.getElementById('course-modal');
    const closeCourseModalBtn = document.getElementById('close-course-modal');

    if (closeCourseModalBtn) {
        closeCourseModalBtn.addEventListener('click', function () {
            courseModal.classList.add('hidden');
        });
    }

    if (courseModal) {
        courseModal.addEventListener('click', function (e) {
            if (e.target === courseModal) {
                courseModal.classList.add('hidden');
            }
        });
    }
}

function loadStudents() {
    fetch('AdminStudentServlet', {
        method: 'GET',
        credentials: 'include'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('HTTP error! status: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            const studentsList = document.getElementById('students-list');
            studentsList.innerHTML = '';

            if (data.students.length === 0) {
                studentsList.innerHTML = '<p class="text-gray-400">No students found.</p>';
                return;
            }

            data.students.forEach(student => {
                const studentDiv = document.createElement('div');
                studentDiv.className = 'flex justify-between items-center p-4 bg-slate-700/50 rounded-lg';
                studentDiv.innerHTML = `
                    <span class="font-medium">${student.username}</span>
                    <button class="view-details-btn px-3 py-1 bg-blue-600 hover:bg-blue-700 rounded text-sm"
                            data-student='${JSON.stringify(student)}'>
                        View Details
                    </button>
                `;
                studentsList.appendChild(studentDiv);
            });

            document.querySelectorAll('.view-details-btn').forEach(btn => {
                btn.addEventListener('click', function () {
                    const student = JSON.parse(this.getAttribute('data-student'));
                    showStudentModal(student);
                });
            });
        })
        .catch(error => {
            console.error('Error loading students:', error);
            document.getElementById('students-list').innerHTML =
                '<p class="text-red-400">Error loading students. Please try again.</p>';
        });
}

function showStudentModal(student) {
    const modalFullname = document.getElementById('modal-fullname');
    const modalEmail = document.getElementById('modal-email');
    const modalCourses = document.getElementById('modal-courses');
    const studentModal = document.getElementById('student-modal');

    if (modalFullname) {
        modalFullname.textContent = student.fullname;
    } else {
        console.error('showStudentModal: modal-fullname element not found');
    }

    if (modalEmail) {
        modalEmail.textContent = student.email;
    } else {
        console.error('showStudentModal: modal-email element not found');
    }

    if (modalCourses) {
        modalCourses.textContent = student.courses && student.courses.length > 0 ? student.courses.join(', ') : 'No courses registered';
    } else {
        console.error('showStudentModal: modal-courses element not found');
    }

    if (studentModal) {
        studentModal.classList.remove('hidden');
    } else {
        console.error('showStudentModal: student-modal element not found');
    }
}


function loadTeachers() {
    fetch('AdminTeacherServlet', {
        method: 'GET',
        credentials: 'include'
    })
        .then(response => response.json())
        .then(data => {
            const teachersList = document.getElementById('teachers-list');
            teachersList.innerHTML = '';

            if (data.teachers.length === 0) {
                teachersList.innerHTML = '<p class="text-gray-400">No teachers found.</p>';
                return;
            }

            data.teachers.forEach(teacher => {
                const teacherDiv = document.createElement('div');
                teacherDiv.className = 'flex justify-between items-center p-4 bg-slate-700/50 rounded-lg';
                teacherDiv.innerHTML = `
                    <span class="font-medium">${teacher.username}</span>
                    <button class="view-teacher-details-btn px-3 py-1 bg-blue-600 hover:bg-blue-700 rounded text-sm"
                            data-teacher='${JSON.stringify(teacher)}'>
                        View Details
                    </button>
                `;
                teachersList.appendChild(teacherDiv);
            });

            document.querySelectorAll('.view-teacher-details-btn').forEach(btn => {
                btn.addEventListener('click', function () {
                    const teacher = JSON.parse(this.getAttribute('data-teacher'));
                    showTeacherModal(teacher);
                });
            });
        })
        .catch(error => {
            console.error('Error loading teachers:', error);
            document.getElementById('teachers-list').innerHTML =
                '<p class="text-red-400">Error loading teachers. Please try again.</p>';
        });
}


function showTeacherModal(teacher) {
    document.getElementById('teacher-modal-fullname').textContent = teacher.fullname;
    document.getElementById('teacher-modal-email').textContent = teacher.email;
    document.getElementById('teacher-modal-courses').textContent =
        teacher.courses && teacher.courses.length > 0 ? teacher.courses.join(', ') : 'No courses assigned';

    document.getElementById('teacher-modal').classList.remove('hidden');
}


function loadCourses() {
    fetch('AdminCourseServlet', {
        method: 'GET',
        credentials: 'include'
    })
        .then(response => response.json())
        .then(data => {
            const coursesList = document.getElementById('courses-list');
            coursesList.innerHTML = '';

            if (data.courses.length === 0) {
                coursesList.innerHTML = '<p class="text-gray-400">No courses found.</p>';
                return;
            }

            data.courses.forEach(course => {
                const courseDiv = document.createElement('div');
                courseDiv.className = 'flex justify-between items-center p-4 bg-slate-700/50 rounded-lg';
                courseDiv.innerHTML = `
                   <div>
                    <span class="font-medium">${course.name}</span>
                    <span class="text-sm text-gray-400 ml-2">(${course.courseCode})</span>

                    <div class="text-sm text-gray-400">
                        ${
                        course.assignedTeacher
                            ? `Assigned Teacher:
                            <span class="text-pink-400 font-semibold">
                                ${course.assignedTeacher}
                            </span>`
                            : `<a href="#"
                                class="assign-teacher-link text-blue-400 hover:text-blue-300"
                                data-course-id="${course.id}">
                                Assign Teacher
                            </a>`
                        }
                        • <a href="#"
                            class="view-students-link text-green-400 hover:text-green-300"
                            data-course-id="${course.id}">
                            View Students (${course.students || 0})
                        </a>
                    </div>
                    </div>

                    <button class="view-course-details-btn px-3 py-1 bg-blue-600 hover:bg-blue-700 rounded text-sm"
                            data-course='${JSON.stringify(course)}'>
                        View Details
                    </button>
                `;
                coursesList.appendChild(courseDiv);
            });

            document.querySelectorAll('.assign-teacher-link').forEach(link => {
                link.addEventListener('click', function (e) {
                    e.preventDefault();
                    const courseId = this.getAttribute('data-course-id');
                    document.querySelector('[data-section="assign-teacher"]').click();
                });
            });

            document.querySelectorAll('.view-students-link').forEach(link => {
                link.addEventListener('click', function (e) {
                    e.preventDefault();
                    const courseId = this.getAttribute('data-course-id');
                    loadCourseStudents(courseId);
                });
            });

            document.querySelectorAll('.view-course-details-btn').forEach(btn => {
                btn.addEventListener('click', function () {
                    const course = JSON.parse(this.getAttribute('data-course'));
                    showCourseModal(course);
                });
            });
        })
        .catch(error => {
            console.error('Error loading courses:', error);
            document.getElementById('courses-list').innerHTML =
                '<p class="text-red-400">Error loading courses. Please try again.</p>';
        });
}


function loadCoursesForSelect() {
    fetch('AdminCourseServlet', {
        method: 'GET',
        credentials: 'include'
    })
        .then(response => {
            if (response.status === 401 || response.status === 403) {
                alert('Unauthorized access. Redirecting to login.');
                window.location.href = 'signin.jsp';
                throw new Error('Unauthorized');
            }
            if (!response.ok) {
                throw new Error('HTTP error! status: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            const select = document.getElementById('assign-course-select');
            select.innerHTML = '<option value="">Select Course</option>';
            if (data.courses && Array.isArray(data.courses)) {
                data.courses.forEach(course => {
                    const option = document.createElement('option');
                    option.value = course.id;
                    option.textContent = course.name + ' (' + course.courseCode + ')';
                    select.appendChild(option);
                });
            } else {
                console.error('loadCoursesForSelect: Invalid data format, courses not found or not an array');
                select.innerHTML = '<option value="">Error: Invalid data</option>';
            }
        })
        .catch(error => {
            console.error('Error loading courses for select:', error);
            const select = document.getElementById('assign-course-select');
            if (select) {
                select.innerHTML = '<option value="">Error loading courses</option>';
            }
        });
}

function loadTeachersForSelect() {
    fetch('AdminTeacherServlet', {
        method: 'GET',
        credentials: 'include'
    })
        .then(response => {
            if (response.status === 401 || response.status === 403) {
                alert('Unauthorized access. Redirecting to login.');
                window.location.href = 'signin.jsp';
                throw new Error('Unauthorized');
            }
            if (!response.ok) {
                throw new Error('HTTP error! status: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            const select = document.getElementById('assign-teacher-select');
            select.innerHTML = '<option value="">Select Teacher</option>';
            if (data.teachers && Array.isArray(data.teachers)) {
                data.teachers.forEach(teacher => {
                    const option = document.createElement('option');
                    option.value = teacher.id;
                    option.textContent = teacher.fullname + ' (' + teacher.username + ')';
                    select.appendChild(option);
                });
            } else {
                console.error('loadTeachersForSelect: Invalid data format, teachers not found or not an array');
                select.innerHTML = '<option value="">Error: Invalid data</option>';
            }
        })
        .catch(error => {
            console.error('Error loading teachers for select:', error);
            const select = document.getElementById('assign-teacher-select');
            if (select) {
                select.innerHTML = '<option value="">Error loading teachers</option>';
            }
        });
}

function showCourseModal(course) {
    document.getElementById('course-modal-name').textContent = course.name;
    document.getElementById('course-modal-code').textContent = course.courseCode;
    document.getElementById('course-modal-teacher').textContent = course.assignedTeacher || 'Not Assigned';
    document.getElementById('course-modal-students').textContent = course.students || 0;

    document.getElementById('course-modal').classList.remove('hidden');
}

function loadCourseStudents(courseId) {
    fetch('AdminCourseStudentsServlet?courseId=' + courseId, {
        method: 'GET',
        credentials: 'include'
    })
        .then(response => response.json())
        .then(data => {
            const students = data.students || [];
            renderStudentsAdmin(students);

            document.getElementById('student-count-admin').textContent = students.length;

            document.querySelectorAll('#main-content > div').forEach(section => section.classList.add('hidden'));
            document.getElementById('course-students-content').classList.remove('hidden');
        })
        .catch(error => {
            console.error('Error loading course students:', error);
            alert('Error loading course students. Please try again.');
        });
}

function renderStudentsAdmin(students) {
    const studentsList = document.getElementById('course-students-list');
    studentsList.innerHTML = '';

    if (students.length === 0) {
        studentsList.innerHTML = '<div class="col-span-full text-center py-12"><div class="bg-slate-800/30 border border-slate-600/30 rounded-xl p-8"><svg class="w-16 h-16 text-gray-500 mx-auto mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" /></svg><h3 class="text-lg font-medium text-gray-400 mb-2">No students enrolled</h3><p class="text-gray-500">This course has no enrolled students yet.</p></div></div>';
        return;
    }

    students.forEach(student => {
        const studentCard = document.createElement('div');
        studentCard.className = 'bg-gradient-to-br from-slate-800/60 to-slate-700/60 backdrop-blur-sm rounded-xl p-6 border border-slate-600/30 hover:border-blue-500/40 transition-all duration-300 hover:shadow-lg hover:shadow-blue-500/10 group';

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

function initAddCourseForm() {
    const addCourseForm = document.getElementById('add-course-form');
    if (addCourseForm) {
        addCourseForm.addEventListener('submit', function (e) {
            e.preventDefault();

            const formData = new FormData(this);
            const courseName = formData.get('courseName');
            const courseCode = formData.get('courseCode');

            if (!courseName || !courseCode) {
                alert('Please fill in all fields');
                return;
            }

            const params = new URLSearchParams();
            params.append('courseName', courseName);
            params.append('courseCode', courseCode);

            fetch('AddCourseServlet', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                credentials: 'include',
                body: params.toString()
            })
                .then(async response => {
                    const text = await response.text();
                    return JSON.parse(text);
                })
                .then(data => {
                    alert('Course added successfully!');
                    addCourseForm.reset();
                    if (document.getElementById('courses-content').classList.contains('hidden') === false) {
                        loadCourses();
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Error adding course. Please try again.');
                });
        });
    }
}


function initAssignTeacherForm() {
    const assignTeacherForm = document.getElementById('assign-teacher-form');
    if (assignTeacherForm) {
        assignTeacherForm.addEventListener('submit', function (e) {
            e.preventDefault();

            const courseId = document.getElementById('assign-course-select').value;
            const teacherId = document.getElementById('assign-teacher-select').value;

            if (!courseId || !teacherId || courseId === "" || teacherId === "") {
                alert('Please select both course and teacher');
                return;
            }

            const params = new URLSearchParams();
            params.append('courseId', courseId);
            params.append('teacherId', teacherId);

            fetch('AssignTeacherServlet', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                credentials: 'include',
                body: params.toString()
            })
                .then(response => response.json())
                .then(data => {
                    alert('Teacher assigned successfully!');
                    assignTeacherForm.reset();

                    if (document.getElementById('courses-content').classList.contains('hidden') === false) {
                        loadCourses();
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Error assigning teacher. Please try again.');
                });
        });
    }
}

document.addEventListener('DOMContentLoaded', function () {
    loadStats();
    initAdminSidebar();
    initAddCourseForm();
    initAssignTeacherForm();
    loadCoursesForSelect();
    loadTeachersForSelect();

    
    const backToCoursesBtn = document.getElementById('back-to-courses-admin');
    if (backToCoursesBtn) {
        backToCoursesBtn.addEventListener('click', function () {
            document.getElementById('course-students-content').classList.add('hidden');
            document.getElementById('courses-content').classList.remove('hidden');
        });
    }
});