import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * Service does not implement LifecycleOwner. So it is not possible
 * to get lifecycleScope inside Service. But instead there is
 * LifecycleService which implements LifecycleOwner, and here it is
 * possible to get lifecycleScope instance.
 */
class TestService : LifecycleService() {

    override fun onCreate() {
        super.onCreate()
        lifecycleScope.launch {
            //Do something here
        }
    }
}