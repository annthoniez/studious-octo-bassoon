$('select').select2({
    minimumResultsForSearch: Infinity
});

var grade_selectbox = $('select[name="filters[grade]"]');
var subject_selectbox = $('select[name="filters[subject]"]');
var topic_selectbox = $('select[name="filters[topic]"]');

grade_selectbox.change(function() {
    $.ajax({
        url: '/api/subject',
        data: {
            grade: grade_selectbox.val()
        },
        success: function(subjects) {
            console.log(subjects);
            subject_selectbox.prop('disabled', true).html('<option></option><option value="0">ทุกรายวิชา</option>').select2('val', '0');

            $.each(subjects, function (i, subject) {
                subject_selectbox.append($('<option>', {
                    value: subject.id,
                    text : subject.title
                }));
            });

            subject_selectbox.prop('disabled', false);
        },
        fail: function() {
            alert('เกิดข้อผิดพลาด โปรดเรียกหน้านี้ใหม่ หรือติดต่อผู้ดูแลระบบ');
        }
    });
});

subject_selectbox.change(function() {
    topic_selectbox.prop('disabled', false).html('<option></option><option value="0">ทุกเรื่อง</option>').select2('val', '0');

    if ($(this).val() != 0) {
        $.ajax({
            url: '/api/subject/' + $(this).val() + '/topic',
            success: function(topics) {
                $.each(topics, function (i, topic) {
                    topic_selectbox.append($('<option>', {
                        value: topic.id,
                        text : topic.title
                    }));
                });

                topic_selectbox.prop('disabled', false);
            },
            fail: function() {
                alert('เกิดข้อผิดพลาด โปรดเรียกหน้านี้ใหม่ หรือติดต่อผู้ดูแลระบบ');
            }
        });
    } else {
        topic_selectbox.prop('disabled', true).html('<option></option>').select2('val', '0');
    };
});


// backend.test.create

$('textarea').ckeditor();

grade_selectbox.change(function() {
    $.ajax({
        url: '/api/subject',
        data: {
            grade: grade_selectbox.val()
        },
        success: function(subjects) {
            console.log(subjects);
            $('select[name="subject_id"]').prop('disabled', true).html("<option value='0'>โปรดเลือกรายวิชา</option>").select2('val', '0');

            $.each(subjects, function (i, subject) {
                $('select[name="subject_id"]').append($('<option>', {
                    value: subject.id,
                    text : subject.title
                }));
            });

            $('select[name="subject_id"]').prop('disabled', false);
        },
        fail: function() {
            alert('เกิดข้อผิดพลาด โปรดเรียกหน้านี้ใหม่ หรือติดต่อผู้ดูแลระบบ');
        }
    });
});

$('select[name="subject_id"]').change(function() {
    $.ajax({
        url: '/api/subject/' + $(this).val() + '/topic',
        success: function(subjects) {
            console.log(subjects);
            $('select[name="topic_id"]').prop('disabled', true).html("<option value='0'>ไม่มีหมวดหมู่</option>").select2('val', '0');

            $.each(subjects, function (i, subject) {
                $('select[name="topic_id"]').append($('<option>', {
                    value: subject.id,
                    text : subject.title
                }));
            });

            $('select[name="topic_id"]').prop('disabled', false);
        },
        fail: function() {
            alert('เกิดข้อผิดพลาด โปรดเรียกหน้านี้ใหม่ หรือติดต่อผู้ดูแลระบบ');
        }
    });
});