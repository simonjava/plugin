package study;

/**
 * Created by chengsimin on 2019/5/27.
 */

class StudentJava extends UserJava {

    private int grade;

    public StudentJava(int age, String name, NameChangeListener nameChangeListener, int grade) {
        super(age, name, nameChangeListener);
        this.grade = grade;
    }

    public StudentJava(int age, String name, int grade) {
        super(age, name);
        this.grade = grade;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }


}
