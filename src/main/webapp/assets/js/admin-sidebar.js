
// Console log to verify script loading

// Function to load stats
function loadStats() {
    fetch('/AdminStatsServlet')
        .then(response => {
            if (!response.ok) {
                throw new Error('HTTP error! status: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            // Check if DOM elements exist before updating
            const totalStudentsEl = document.getElementById('total-students');
            const totalTeachersEl = document.getElementById('total-teachers');
            const totalCoursesEl = document.getElementById('total-courses');

            if (totalStudentsEl) {
                totalStudentsEl.textContent = data.totalStudents;
            } else {
                console.error('loadStats: total-students element not found');
            }

            if (totalTeachersEl) {
                totalTeachersEl.textContent = data.totalTeachers;
            } else {
                console.error('loadStats: total-teachers element not found');
            }

            if (totalCoursesEl) {
                totalCoursesEl.textContent = data.totalCourses;
            } else {
                console.error('loadStats: total-courses element not found');
            }

        })
        .catch(error => {
            console.error('loadStats: Error occurred during stats loading:', error);
            console.error('loadStats: Error details:', error.message);
            
            // Check if DOM elements exist before setting error text
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

// Function to initialize admin sidebar
function initAdminSidebar() {
    // Load stats on page load
    loadStats();

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

                // Load content for specific sections
                if (this.getAttribute('data-section') === 'students') {
                    loadStudents();
                } else if (this.getAttribute('data-section') === 'teachers') {
                    loadTeachers();
                } else if (this.getAttribute('data-section') === 'courses') {
                    loadCourses();
                } else if (this.getAttribute('data-section') === 'assign-teacher') {
                    loadCoursesForSelect();
                    loadTeachersForSelect();
                }
            } else {
                // Fallback to default if section not found
                document.getElementById('default-content').classList.remove('hidden');
            }
        });
    });

    // Modal functionality
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
}


// Function to load students
function loadStudents() {
    fetch('AdminStudentServlet')
        .then(response => {
            if (!response.ok) {
                throw new Error('HTTP error! status: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            const studentsList = document.getElementById('students-list');
            studentsList.innerHTML = '';

            if (data.length === 0) {
                studentsList.innerHTML = '<p class="text-gray-400">No students found.</p>';
                return;
            }

            data.forEach(student => {
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

            // Add event listeners to view details buttons
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

// Function to show student modal
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

// Teacher modal functionality
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

// Function to load teachers
function loadTeachers() {
    fetch('AdminTeacherServlet')
        .then(response => response.json())
        .then(data => {
            const teachersList = document.getElementById('teachers-list');
            teachersList.innerHTML = '';

            if (data.length === 0) {
                teachersList.innerHTML = '<p class="text-gray-400">No teachers found.</p>';
                return;
            }

            data.forEach(teacher => {
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

            // Add event listeners to view details buttons
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

// Function to show teacher modal
function showTeacherModal(teacher) {
    document.getElementById('teacher-modal-fullname').textContent = teacher.fullname;
    document.getElementById('teacher-modal-email').textContent = teacher.email;
    document.getElementById('teacher-modal-courses').textContent =
        teacher.courses && teacher.courses.length > 0 ? teacher.courses.join(', ') : 'No courses assigned';

    document.getElementById('teacher-modal').classList.remove('hidden');
}

// Course modal functionality
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

// Function to load courses
function loadCourses() {
    fetch('AdminCourseServlet')
        .then(response => response.json())
        .then(data => {
            const coursesList = document.getElementById('courses-list');
            coursesList.innerHTML = '';

            if (data.length === 0) {
                coursesList.innerHTML = '<p class="text-gray-400">No courses found.</p>';
                return;
            }

            data.forEach(course => {
                const courseDiv = document.createElement('div');
                courseDiv.className = 'flex justify-between items-center p-4 bg-slate-700/50 rounded-lg';
                courseDiv.innerHTML = `
                    <div>
                        <span class="font-medium">${course.name}</span>
                        <span class="text-sm text-gray-400 ml-2">(${course.courseCode})</span>
                        <div class="text-sm text-gray-400">
                            ${course.assignedTeacher ? 'Teacher Assigned' : '<a href="#" class="assign-teacher-link text-blue-400 hover:text-blue-300" data-course-id="' + course.id + '">Assign Teacher</a>'}
                            • <a href="#" class="view-students-link text-green-400 hover:text-green-300" data-course-id="${course.id}">View Students (${course.students || 0})</a>
                        </div>
                    </div>
                    <button class="view-course-details-btn px-3 py-1 bg-blue-600 hover:bg-blue-700 rounded text-sm"
                            data-course='${JSON.stringify(course)}'>
                        View Details
                    </button>
                `;
                coursesList.appendChild(courseDiv);
            });

            // Add event listeners to assign teacher links
            document.querySelectorAll('.assign-teacher-link').forEach(link => {
                link.addEventListener('click', function (e) {
                    e.preventDefault();
                    const courseId = this.getAttribute('data-course-id');
                    // Switch to assign teacher section
                    document.querySelector('[data-section="assign-teacher"]').click();
                });
            });

            // Add event listeners to view students links
            document.querySelectorAll('.view-students-link').forEach(link => {
                link.addEventListener('click', function (e) {
                    e.preventDefault();
                    const courseId = this.getAttribute('data-course-id');
                    loadCourseStudents(courseId);
                });
            });

            // Add event listeners to view details buttons
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

// Function to load courses for select
function loadCoursesForSelect() {
    fetch('AdminCourseServlet')
        .then(response => response.json())
        .then(data => {
            const select = document.getElementById('assign-course-select');
            select.innerHTML = '<option value="">Select Course</option>';
            data.forEach(course => {
                const option = document.createElement('option');
                option.value = course.id;
                option.textContent = course.name + ' (' + course.courseCode + ')';
                select.appendChild(option);
            });
        })
        .catch(error => {
            console.error('Error loading courses for select:', error);
        });
}

// Function to load teachers for select
function loadTeachersForSelect() {
    fetch('AdminTeacherServlet')
        .then(response => response.json())
        .then(data => {
            const select = document.getElementById('assign-teacher-select');
            select.innerHTML = '<option value="">Select Teacher</option>';
            data.forEach(teacher => {
                const option = document.createElement('option');
                option.value = teacher.id;
                option.textContent = teacher.fullname + ' (' + teacher.username + ')';
                select.appendChild(option);
            });
        })
        .catch(error => {
            console.error('Error loading teachers for select:', error);
        });
}

// Function to show course modal
function showCourseModal(course) {
    document.getElementById('course-modal-name').textContent = course.name;
    document.getElementById('course-modal-code').textContent = course.courseCode;
    document.getElementById('course-modal-teacher').textContent = course.assignedTeacher || 'Not Assigned';
    document.getElementById('course-modal-students').textContent = course.students || 0;

    document.getElementById('course-modal').classList.remove('hidden');
}

// Function to load course students
function loadCourseStudents(courseId) {
    fetch('AdminCourseStudentsServlet?courseId=' + courseId)
        .then(response => response.json())
        .then(data => {
            const courseStudentsList = document.getElementById('course-students-list');
            courseStudentsList.innerHTML = '';

            if (data.success) {
                if (data.students.length === 0) {
                    courseStudentsList.innerHTML = '<p class="text-gray-400">No students enrolled in this course.</p>';
                } else {
                    // Add teacher info at the top
                    const teacherDiv = document.createElement('div');
                    teacherDiv.className = 'p-4 bg-blue-600/20 rounded-lg mb-4';
                    teacherDiv.innerHTML = `
                        <div class="text-sm text-blue-300">
                            <strong>Teacher:</strong> ${data.teacher || 'Not Assigned'}
                        </div>
                    `;
                    courseStudentsList.appendChild(teacherDiv);

                    data.students.forEach(student => {
                        const studentDiv = document.createElement('div');
                        studentDiv.className = 'flex justify-between items-center p-4 bg-slate-700/50 rounded-lg';
                        studentDiv.innerHTML = `
                            <div>
                                <span class="font-medium">${student.fullname}</span>
                                <span class="text-sm text-gray-400 ml-2">(${student.username})</span>
                            </div>
                            <span class="text-sm text-gray-400">${student.email}</span>
                        `;
                        courseStudentsList.appendChild(studentDiv);
                    });
                }

                // Show the course students content
                document.querySelectorAll('#main-content > div').forEach(section => section.classList.add('hidden'));
                document.getElementById('course-students-content').classList.remove('hidden');
            } else {
                alert('Error: ' + (data.message || 'Failed to load students'));
            }
        })
        .catch(error => {
            console.error('Error loading course students:', error);
            alert('Error loading course students. Please try again.');
        });
}

// Add Course Form Handling
function initAddCourseForm() {
    const addCourseForm = document.getElementById('add-course-form');
    if (addCourseForm) {
        addCourseForm.addEventListener('submit', function (e) {
            e.preventDefault();

            const formData = new FormData(this);
            const courseName = formData.get('courseName');
            const courseCode = formData.get('courseCode');

            // Basic validation
            if (!courseName || !courseCode) {
                alert('Please fill in all fields');
                return;
            }

            // Submit form via AJAX
            fetch('AddCourseServlet', {
                method: 'POST',
                body: formData
            })
                .then(response => {
                    return response.text().then(text => {
                        return JSON.parse(text);
                    });
                })
                .then(data => {
                    if (data.success) {
                        alert('Course added successfully!');
                        // Reset form
                        addCourseForm.reset();
                        // Refresh courses list if we're on the courses page
                        if (document.getElementById('courses-content').classList.contains('hidden') === false) {
                            loadCourses();
                        }
                    } else {
                        alert('Error: ' + (data.error || 'Failed to add course'));
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Error adding course. Please try again.');
                });
        });
    }
}


// Assign Teacher Form Handling
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

            const formData = new FormData();
            formData.append('courseId', courseId);
            formData.append('teacherId', teacherId);

            fetch('AssignTeacherServlet', {
                method: 'POST',
                body: formData
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        alert('Teacher assigned successfully!');
                        // Reset form
                        assignTeacherForm.reset();
                        // Refresh courses list if we're on the courses page
                        if (document.getElementById('courses-content').classList.contains('hidden') === false) {
                            loadCourses();
                        }
                    } else {
                        alert('Error: ' + (data.error || 'Failed to assign teacher'));
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
    initAdminSidebar();
    initAddCourseForm();
    initAssignTeacherForm();
    // Load courses and teachers for assign teacher form
    loadCoursesForSelect();
    loadTeachersForSelect();
});