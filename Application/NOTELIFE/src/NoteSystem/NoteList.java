package NoteSystem;

    import java.io.File;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.Calendar;
    import java.util.Collections;
    import java.util.Comparator;
    import java.util.Scanner;

    public class NoteList {

    ArrayList<Note> list;
    String defaultPath;
    final int SORT_DATE_NEWESTFIRST = 0;
    final int SORT_DATE_OLDESTFIRST = 1;
    final int SORT_FILESIZE_BIGGESTFIRST = 2;
    final int SORT_FILESIZE_SMALLESTFIRST = 3;
    final int SORT_VIEWS_MOST = 4;

    public NoteList(String defaultPath) {
        list = new ArrayList<>();
        this.defaultPath = defaultPath;
    }

    public ArrayList<Note> getList()
    {
        return list;
    }
    public void setList(ArrayList<Note> list)
    {
        this.list = list;
    }

    public void searchList(String searchTerm)
    {
        list = Search.sortListBy(list, searchTerm);
    }

    public void refreshNoteList() {

        list.clear();
        File saveFolder = new File(defaultPath);
        File[] filesInFolder = saveFolder.listFiles();
        ArrayList<File> directoriesInFolder = new ArrayList<>();

        for (int i = 0; i < filesInFolder.length; i++) {
            if (filesInFolder[i].isDirectory()) {
                directoriesInFolder.add(filesInFolder[i]);
            }
        }

        int numOfNotes = directoriesInFolder.size();

        for (int i = 0; i < numOfNotes; i++) {

            File currentFolder = directoriesInFolder.get(i);
            File infoFile = new File(currentFolder.getPath() + "/data.info");
            Scanner infoReader;

            try {

                infoReader = new Scanner(infoFile);
                ArrayList<String> info = new ArrayList<>();

                while (infoReader.hasNext()) {
                info.add(infoReader.nextLine());
                }
                infoReader.close();

                String title = info.get(2).substring(6, info.get(2).length());
                String description = info.get(3).substring(12, info.get(3).length());
                int numOfFiles = currentFolder.listFiles().length;

                String dateString = info.get(4).substring(5, info.get(4).length());

                Calendar date = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd h:mm a");
                date.setTime(dateFormat.parse(dateString));

                long size = Long.parseLong(info.get(5).substring(5, info.get(5).length()));

                list.add(new Note(title, description, date, numOfFiles - 1, size));
            }
            catch (Exception e) {
                e.printStackTrace();
                }
         }
    }


    public long noteSize(File directory) {

        long length = 0;
        for (File file : directory.listFiles()) {

            if (file.isFile() || !file.exists()) {
                length += file.length();
            }
            else {
                length += noteSize(file);
            }
        }
        return length;
    }

    public void sortList(int sortMethod) {

        switch (sortMethod) {
            case (SORT_DATE_NEWESTFIRST):
                Collections.sort(list, new SortDateNewestFirst());
            break;

            case (SORT_DATE_OLDESTFIRST):
                Collections.sort(list, new SortDateOldestFirst());
                break;

            case (SORT_FILESIZE_BIGGESTFIRST):
                Collections.sort(list, new SortSizeLargestFirst());
                break;

            case (SORT_FILESIZE_SMALLESTFIRST):
                Collections.sort(list, new SortSizeSmallestFirst());
                break;

            case (SORT_VIEWS_MOST):
                System.out.println("Views first");
                break;

            default:
                System.err.println("INVALID SORT METHOD PASSED");
        }
    }
}

    class SortDateNewestFirst implements Comparator<Note> {

    @Override
    public int compare(Note a, Note b) {
        if (a.date.before(b.date)) {
            return 1;
        }
        return -1;
        }
    }

    class SortDateOldestFirst implements Comparator<Note> {

    @Override
    public int compare(Note a, Note b)
    {
        if (a.date.after(b.date)) {
            return 1;
        }
        return -1;
        }
    }

    class SortSizeLargestFirst implements Comparator<Note> {

    @Override
    public int compare(Note a, Note b) {
        if (a.size < b.size) {
            return 1;
        }
        return -1;
        }
    }

    class SortSizeSmallestFirst implements Comparator<Note> {

    @Override
    public int compare(Note a, Note b) {
        if (a.size > b.size) {
            return 1;
        }
        return -1;
        }
    }

