package com.zakariyaf.DevLog;

import android.os.Parcel;
import android.os.Parcelable;

public final class ProjectInfo implements Parcelable {
    public final static Parcelable.Creator<ProjectInfo> CREATOR =
            new Parcelable.Creator<ProjectInfo>() {

                @Override
                public ProjectInfo createFromParcel(Parcel source) {
                    return new ProjectInfo(source);
                }

                @Override
                public ProjectInfo[] newArray(int size) {
                    return new ProjectInfo[size];
                }
            };
    private CourseInfo mCourse;
    private String mTitle;
    private String mText;

    public ProjectInfo(CourseInfo course, String title, String text) {
        mCourse = course;
        mTitle = title;
        mText = text;
    }

    private ProjectInfo(Parcel source) {
        mCourse = source.readParcelable(CourseInfo.class.getClassLoader());
        mTitle = source.readString();
        mText = source.readString();
    }

    public CourseInfo getCourse() {
        return mCourse;
    }

    public void setCourse(CourseInfo course) {
        mCourse = course;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    private String getCompareKey() {
        return mCourse.getCourseId() + "|" + mTitle + "|" + mText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProjectInfo that = (ProjectInfo) o;

        return getCompareKey().equals(that.getCompareKey());
    }

    @Override
    public int hashCode() {
        return getCompareKey().hashCode();
    }

    @Override
    public String toString() {
        return getCompareKey();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mCourse, 0);
        dest.writeString(mTitle);
        dest.writeString(mText);
    }
}
